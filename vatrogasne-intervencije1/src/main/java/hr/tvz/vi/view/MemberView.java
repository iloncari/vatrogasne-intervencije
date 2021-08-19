/*
 * MemberVIew.java.
 *
 */
package hr.tvz.vi.view;

import com.google.common.eventbus.Subscribe;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;

import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.components.MemberForm;
import hr.tvz.vi.event.ChangeBroadcaster;
import hr.tvz.vi.event.PersonOrganizationChangedEvent;
import hr.tvz.vi.orm.Person;
import hr.tvz.vi.orm.PersonOrganization;
import hr.tvz.vi.service.OrganizationService;
import hr.tvz.vi.service.PersonService;
import hr.tvz.vi.util.Constants.Duty;
import hr.tvz.vi.util.Constants.EventAction;
import hr.tvz.vi.util.Constants.EventSubscriber;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Constants.StyleConstants;
import hr.tvz.vi.util.Constants.SubscriberScope;
import hr.tvz.vi.util.Constants.UserRole;
import hr.tvz.vi.util.Utils;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.components.html.VH4;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.select.VSelect;

import de.codecamp.vaadin.serviceref.ServiceRef;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class MemberView.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 6:40:41 PM Aug 11, 2021
 */
@Slf4j
@Route(value = Routes.MEMBER, layout = MainAppLayout.class)
@EventSubscriber(scope = SubscriberScope.PUSH)
public class MemberView extends VVerticalLayout implements HasDynamicTitle, HasUrlParameter<String> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4672041574716367733L;

  /** The member. */
  private Person member;

  /** The current user. */
  private final CurrentUser currentUser = Utils.getCurrentUser(UI.getCurrent());

  /** The organizations grid. */
  private VGrid<PersonOrganization> organizationsGrid;

  /** The organization service ref. */
  @Autowired
  private ServiceRef<OrganizationService> organizationServiceRef;

  /** The person service ref. */
  @Autowired
  private ServiceRef<PersonService> personServiceRef;

  /**
   * Gets the page title.
   *
   * @return the page title
   */
  @Override
  public String getPageTitle() {
    return getTranslation(Routes.getPageTitleKey(Routes.MEMBER));
  }

  /**
   * Inits the organization layout.
   *
   * @return the v vertical layout
   */
  private VVerticalLayout initOrganizationLayout() {
    final VVerticalLayout layout = new VVerticalLayout().withClassName(StyleConstants.WIDTH_100.getName());
    final VH4 organizationLayoutTitle = new VH4(getTranslation("memberView.organizationsGrid.title"));
    layout.add(organizationLayoutTitle);

    organizationsGrid = new VGrid<>();
    organizationsGrid.setItems(member.getOrgList());
    organizationsGrid.addColumn(po -> po.getOrganization().getName()).setHeader(getTranslation("memberView.organizationsGrid.name"));
    organizationsGrid.addColumn(po -> po.getJoinDate()).setHeader(getTranslation("memberView.organizationsGrid.joinDate"));
    organizationsGrid.addColumn(po -> po.getExitDate()).setHeader(getTranslation("memberView.organizationsGrid.exitDate"));
    organizationsGrid.addComponentColumn(po -> {
      final VSelect<UserRole> roleSelect = new VSelect<>();
      roleSelect.setItems(Arrays.asList(UserRole.values()));
      roleSelect.setItemLabelGenerator(role -> getTranslation("role." + role.getName().toLowerCase() + ".label"));
      roleSelect.setValue(po.getRole());
      roleSelect.setEnabled(currentUser.hasManagerRole());
      roleSelect.addValueChangeListener(e -> {
        po.setRole(e.getValue());
        organizationServiceRef.get().addOrUpdateOrganizationForPerson(po);
        ChangeBroadcaster.firePushEvent(new PersonOrganizationChangedEvent(this, po, EventAction.MODIFIED));
      });
      return roleSelect;
    }).setHeader(getTranslation("memberView.organizationsGrid.role"));

    organizationsGrid.addComponentColumn(po -> {
      final VSelect<Duty> roleSelect = new VSelect<>();
      roleSelect.setItems(Arrays.asList(Duty.values()));
      roleSelect.setItemLabelGenerator(role -> getTranslation("duty." + role.getName().toLowerCase() + ".label"));
      roleSelect.setValue(po.getDuty());
      roleSelect.setEnabled(currentUser.hasManagerRole());
      roleSelect.addValueChangeListener(e -> {
        po.setDuty(e.getValue());
        organizationServiceRef.get().addOrUpdateOrganizationForPerson(po);
        ChangeBroadcaster.firePushEvent(new PersonOrganizationChangedEvent(this, po, EventAction.MODIFIED));
      });
      return roleSelect;
    }).setHeader(getTranslation("memberView.organizationsGrid.duty"));
    layout.add(organizationsGrid);
    return layout;
  }

  /**
   * On attach.
   *
   * @param attachEvent the attach event
   */
  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    ChangeBroadcaster.registerToPushEvents(this);
    add(new MemberForm(member, personServiceRef.get(), organizationServiceRef.get(), false));
    add(initOrganizationLayout());
  }

  /**
   * On detach.
   *
   * @param detachEvent the detach event
   */
  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    ChangeBroadcaster.unregisterFromPushEvents(this);
  }

  /**
   * Person organization changed.
   *
   * @param event the event
   */
  @Subscribe
  public void personOrganizationChanged(PersonOrganizationChangedEvent event) {
    if (event.getPersonOrganization().getPerson().getId().equals(member.getId())) {
      getUI().ifPresent(ui -> ui.access(() -> {
        this.member = personServiceRef.get().getPersonById(member.getId()).orElse(null);
        organizationsGrid.setItems(member.getOrgList());
        organizationsGrid.getDataProvider().refreshAll();
      }));
    }
  }

  /**
   * Sets the parameter.
   *
   * @param event the event
   * @param memberId the member id
   */
  @Override
  public void setParameter(BeforeEvent event, String memberId) {
    if (StringUtils.isBlank(memberId) || !NumberUtils.isParsable(memberId)) {
      // navigate to NavigationErrorPage
      throw new NotFoundException();
    }

    this.member = personServiceRef.get().getPersonById(Long.valueOf(memberId)).orElse(null);
    if (member == null || !Utils.isUserHasEditRights(currentUser, member)) {
      throw new AccessDeniedException("Access Denied");
    }
  }

}
