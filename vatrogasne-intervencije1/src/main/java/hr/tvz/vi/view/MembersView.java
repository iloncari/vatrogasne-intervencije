/*
 * MembersView MembersView.java.
 *
 */
package hr.tvz.vi.view;

import com.google.common.eventbus.Subscribe;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import hr.tvz.vi.event.ChangeBroadcaster;
import hr.tvz.vi.event.PersonOrganizationChangedEvent;
import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.orm.Person;
import hr.tvz.vi.orm.PersonOrganization;
import hr.tvz.vi.service.OrganizationService;
import hr.tvz.vi.service.PersonService;
import hr.tvz.vi.util.Constants.EventAction;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Utils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.button.DeleteButton;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.dialog.VDialog;
import org.vaadin.firitin.components.html.VH5;
import org.vaadin.firitin.components.html.VSpan;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VTextField;

import de.codecamp.vaadin.serviceref.ServiceRef;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class MembersView.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 1:50:34 PM Aug 11, 2021
 */
@Slf4j
@Route(value = Routes.MEMBERS, layout = MainAppLayout.class)
public class MembersView extends AbstractGridView<Person> implements HasDynamicTitle {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6684616711164368364L;

  /** The remove member button. */
  private DeleteButton removeMemberButton;

  /** The active organization. */
  private final Organization activeOrganization = getCurrentUser().getActiveOrganization().getOrganization();

  /** The organization service ref. */
  @Autowired
  private ServiceRef<OrganizationService> organizationServiceRef;

  /** The person service ref. */
  @Autowired
  private ServiceRef<PersonService> personServiceRef;

  /**
   * Gets the grid items.
   *
   * @return the grid items
   */
  @Override
  public List<Person> getGridItems() {
    return organizationServiceRef.get().getOrganizationMembers(activeOrganization);
  }

  /**
   * Gets the page title.
   *
   * @return the page title
   */
  @Override
  public String getPageTitle() {
    return getTranslation(Routes.getPageTitleKey(Routes.MEMBERS));
  }

  @Override
  protected String getViewTitle() {
    return getPageTitle();
  }

  /**
   * Inits the bellow button layout.
   *
   * @return the v horizontal layout
   */
  @Override
  protected VHorizontalLayout initBellowButtonLayout() {
    final VHorizontalLayout buttonsLayout = new VHorizontalLayout();

    final VButton newMemberButton = new VButton(getTranslation("membersView.button.newMember.label"));
    newMemberButton.addClickListener(e -> showNewMemberDialog());
    buttonsLayout.add(newMemberButton);

    if (!activeOrganization.getChilds().isEmpty()) {
      final VButton addMemberFromChildButton = new VButton(getTranslation("membersView.button.addFromChilds.label"));
      addMemberFromChildButton.addClickListener(e -> showAddMemberDialog());
      buttonsLayout.add(addMemberFromChildButton);
    }

    removeMemberButton = new DeleteButton().withEnabled(false);
    buttonsLayout.add(removeMemberButton
      .withText(getTranslation("membersView.removeMemberDialog.button.label"))
      .withConfirmText(getTranslation("yes.label"))
      .withRejectText(getTranslation("no.label"))
      .withPromptText(getTranslation("areYouSure.label"))
      .withHeaderText(getTranslation("membersView.removeMemberDialog.title"))
      .withConfirmHandler(() -> {
        getGrid().getSelectedItems().stream().findFirst().ifPresent(person -> {
          organizationServiceRef.get().leftOrganization(person, activeOrganization);
          organizationServiceRef.get().getPersonOrganization(person, activeOrganization).ifPresent(po -> {
            ChangeBroadcaster.firePushEvent(new PersonOrganizationChangedEvent(this,
              po, EventAction.MODIFIED));
          });
        });
      }));

    return buttonsLayout;
  }

  /**
   * Inits the grid.
   */
  @Override
  protected void initGrid() {
    getGrid().removeAllColumns();
    getGrid().setSelectionMode(SelectionMode.SINGLE);
    getGrid().addSelectionListener(e -> removeMemberButton.setEnabled(!e.getFirstSelectedItem().isEmpty()));

    getGrid().addComponentColumn(per -> {
      if (Utils.isUserHasEditRights(getCurrentUser(), per)) {
        return new RouterLink(per.getIdentificationNumber(), MemberView.class, per.getId().toString());
      }
      return new VSpan(per.getIdentificationNumber());
    }).setHeader(getTranslation("membersView.membersGrid.identificationNumber"));
    getGrid().addColumn(per -> per.getName().concat(" ").concat(per.getLastname())).setHeader(getTranslation("membersView.membersGrid.nameAndLastname"));
    getGrid().addColumn(per -> per.getBirthDate()).setHeader(getTranslation("membersView.membersGrid.birthDate"));
    getGrid().addColumn(per -> per.getEmail()).setHeader(getTranslation("membersView.membersGrid.email"));
    getGrid().addColumn(per -> {
      final Optional<PersonOrganization> perOrg = organizationServiceRef.get().getPersonOrganization(per,
        activeOrganization);
      return perOrg.isPresent() ? perOrg.get().getJoinDate() : StringUtils.EMPTY;
    }).setHeader(getTranslation("membersView.membersGrid.joinDate"));

    getGrid().addColumn(per -> {
      final Optional<PersonOrganization> perOrg = organizationServiceRef.get().getPersonOrganization(per,
        activeOrganization);
      return perOrg.isPresent() ? perOrg.get().getDuty() : StringUtils.EMPTY;
    }).setHeader(getTranslation("membersView.membersGrid.duty"));

    getGrid().addColumn(per -> personServiceRef.get().isPersonHaveAccessToOrganization(per, activeOrganization) == true
      ? getTranslation("yes.label")
      : getTranslation("no.label")).setHeader(getTranslation("membersView.membersGrid.organizationAccess"));

    getGrid().addItemClickListener(e -> {
      if (e.getClickCount() > 1 && Utils.isUserHasEditRights(getCurrentUser(), e.getItem())) {
        UI.getCurrent().navigate(MemberView.class, e.getItem().getId().toString());
      }
    });
  }

  /**
   * Person organization changed.
   *
   * @param event the event
   */
  @Subscribe
  public void personOrganizationChanged(PersonOrganizationChangedEvent event) {
    if (event.getPersonOrganization().getOrganization().getId().equals(activeOrganization.getId())) {
      getUI().ifPresent(ui -> ui.access(() -> setGridItems()));
    }

  }

  /**
   * Show check person id from childs dialog.
   */
  private void showAddMemberDialog() {
    final VDialog dialog = new VDialog();
    dialog.setCloseOnEsc(false);
    dialog.setCloseOnOutsideClick(false);

    final VVerticalLayout dialogLayout = new VVerticalLayout();
    final VH5 dialogTitle = new VH5(getTranslation("membersView.newMembersDialog.title"));
    dialogLayout.add(dialogTitle);
    final VTextField idField = new VTextField(getTranslation("membersView.newMembersDialog.identificationNumber"));
    idField.setValueChangeMode(ValueChangeMode.EAGER);

    final VButton checkButton = new VButton(getTranslation("button.confirm")).withClickListener(e -> {
      final Optional<Person> member = personServiceRef.get().getPersonByIdNumber(idField.getValue());
      if (member.isPresent()) {
        final List<Long> childOrgIds = activeOrganization.getChilds().stream().map(org -> org.getId())
          .collect(Collectors.toList());
        final boolean isMemberPartOfChild = member.get().getOrgList().stream().filter(po -> po.getExitDate() == null)
          .anyMatch(po -> childOrgIds.contains(po.getOrganization().getId()));

        if (isMemberPartOfChild
          && !organizationServiceRef.get().isPersonOrganizationMember(member.get(), activeOrganization)) {
          organizationServiceRef.get().joinOrganization(member.get(), activeOrganization, false);
          Utils.showSuccessNotification(2000, Position.TOP_CENTER, "membersView.newMembersDialog.notification.memberAdded");
          dialog.close();
        } else {
          idField.setInvalid(true);
          idField.setErrorMessage(getTranslation("membersView.newMembersDialog.identificationNumber.alreadyMember.error"));
        }
      } else {
        idField.setInvalid(true);
        idField.setErrorMessage(getTranslation("membersView.newMembersDialog.identificationNumber.memberNotExist.error"));
      }
    });

    idField.addValueChangeListener(e -> {
      checkButton.setEnabled(false);
      if (StringUtils.isBlank(e.getValue())) {
        e.getSource().setInvalid(true);
        e.getSource().setErrorMessage(getTranslation("membersView.newMembersDialog.identificationNumber.required.error"));
        return;
      }
      if (!e.getValue().matches("^[0-9]*$")) {
        e.getSource().setInvalid(true);
        e.getSource().setErrorMessage(getTranslation("membersView.newMembersDialog.identificationNumber.onlyNumbers.error"));
        return;
      }
      e.getSource().setInvalid(false);
      checkButton.setEnabled(true);
    });
    final VButton closeButton = new VButton(getTranslation("button.close")).withClickListener(e -> dialog.close());
    final VHorizontalLayout buttonsLayout = new VHorizontalLayout(checkButton, closeButton);
    dialogLayout.add(idField, buttonsLayout);

    dialog.add(dialogLayout);
    dialog.open();
  }

  /**
   * Show new member dialog.
   */
  private void showNewMemberDialog() {
    final VDialog dialog = new VDialog();
    dialog.setCloseOnEsc(false);
    dialog.setCloseOnOutsideClick(false);

    final VVerticalLayout dialogLayout = new VVerticalLayout();
    final VH5 dialogTitle = new VH5(getTranslation("membersView.newMembersDialog.title"));
    dialogLayout.add(dialogTitle);
    final VTextField idField = new VTextField(getTranslation("membersView.newMembersDialog.identificationNumber"));
    idField.setValueChangeMode(ValueChangeMode.EAGER);

    final VButton checkButton = new VButton(getTranslation("button.confirm")).withClickListener(e -> {
      final Optional<Person> member = personServiceRef.get().getPersonByIdNumber(idField.getValue());
      if (member.isPresent()) {
        final Long parentOrgId = getCurrentUser().getParentOrganization() != null ? getCurrentUser().getParentOrganization().getId() : null;
        final boolean isPersonMemberOfParent = parentOrgId != null
          ? member.get().getOrgList().stream().filter(po -> po.getExitDate() == null)
            .anyMatch(po -> po.getOrganization().getId().equals(parentOrgId))
          : false;
        final boolean isPersonMemberOfAnyOtherOrg = member.get().getOrgList().stream()
          .anyMatch(po -> po.getExitDate() == null && po.getOrganization().getId() != parentOrgId);
        if (isPersonMemberOfAnyOtherOrg || !isPersonMemberOfParent) {
          idField.setInvalid(true);
          idField.setErrorMessage(getTranslation("membersView.newMembersDialog.identificationNumber.alreadyMember.error"));
        } else {
          organizationServiceRef.get().joinOrganization(member.get(), activeOrganization, false);
          Utils.showSuccessNotification(2000, Position.TOP_CENTER, "membersView.newMembersDialog.notification.memberAdded");
          dialog.close();
        }
      } else {
        UI.getCurrent().navigate(AddMemberView.class, idField.getValue());
        dialog.close();
      }
    }).withEnabled(false);

    idField.addValueChangeListener(e -> {
      checkButton.setEnabled(false);
      if (StringUtils.isBlank(e.getValue())) {
        e.getSource().setInvalid(true);
        e.getSource().setErrorMessage(getTranslation("membersView.newMembersDialog.identificationNumber.required.error"));
        return;
      }
      if (!e.getValue().matches("^[0-9]*$")) {
        e.getSource().setInvalid(true);
        e.getSource().setErrorMessage(getTranslation("membersView.newMembersDialog.identificationNumber.onlyNumbers.error"));
        return;
      }
      e.getSource().setInvalid(false);
      checkButton.setEnabled(true);
    });

    final VButton closeButton = new VButton(getTranslation("button.close")).withClickListener(e -> dialog.close());
    final VHorizontalLayout buttonsLayout = new VHorizontalLayout(checkButton, closeButton);
    dialogLayout.add(idField, buttonsLayout);

    dialog.add(dialogLayout);
    dialog.open();
  }

}
