/*
 * OrganizationView OrganizationView.java.
 *
 */

package hr.tvz.vi.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.orm.Person;
import hr.tvz.vi.orm.PersonOrganization;
import hr.tvz.vi.service.OrganizationService;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Constants.StyleConstants;
import hr.tvz.vi.util.Constants.UserRole;
import hr.tvz.vi.util.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.checkbox.VCheckBox;
import org.vaadin.firitin.components.datepicker.VDatePicker;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.components.html.VDiv;
import org.vaadin.firitin.components.html.VH3;
import org.vaadin.firitin.components.html.VH5;
import org.vaadin.firitin.components.html.VSpan;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.select.VSelect;
import org.vaadin.firitin.components.textfield.VTextField;

import de.codecamp.vaadin.serviceref.ServiceRef;

/**
 * The Class OrganizationView.
 *
 * @author Igor LonÄ�ariÄ‡ (iloncari2@tvz.hr)
 * @since 10:21:47 PM Aug 10, 2021
 */
@Route(value = Routes.ORGANIZATION, layout = MainAppLayout.class)
public class OrganizationView extends VVerticalLayout implements HasDynamicTitle {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3567099719758726806L;

  /** The current user. */
  private final CurrentUser currentUser;

  /** The active organization binder. */
  private final Binder<Organization> activeOrganizationBinder = new Binder<>(Organization.class);

  /** The active organization. */
  private final Organization activeOrganization;

  /** The organization service ref. */
  @Autowired
  private ServiceRef<OrganizationService> organizationServiceRef;

  /** The person org requests grid. */
  private VGrid<PersonOrganization> personOrgRequestsGrid;

  /** The new members grid. */
  private VGrid<Person> newMembersGrid;

  /** The person organization requests. */
  private final List<PersonOrganization> personOrganizationRequests = new ArrayList<>();

  /** The child organizations members. */
  private final List<Person> childOrganizationsMembers = new ArrayList<>();

  /** The is parent. */
  private final boolean organizationIsParent;

  /** The child organization ids. */
  private final List<Long> childOrganizationIds = new ArrayList<>();

  /**
   * Instantiates a new organization view.
   */
  public OrganizationView() {
    currentUser = Utils.getCurrentUser(UI.getCurrent());
    activeOrganization = currentUser.getActiveOrganization().getOrganization();
    organizationIsParent = !activeOrganization.getChilds().isEmpty();
    childOrganizationIds.addAll(activeOrganization.getChilds().stream().map(Organization::getId)
      .collect(Collectors.toList()));

    final VH3 activeOrganizationTitle = new VH3(getTranslation("organization.active.title"));
    add(activeOrganizationTitle);

    final VDiv activeOrganizationData = new VDiv().withClassName(StyleConstants.WIDTH_50.getName());

    final VTextField activeOrgName = new VTextField(getTranslation("organization.name")).withClassName(StyleConstants.WIDTH_75.getName());
    activeOrganizationBinder.bind(activeOrgName, "name");
    activeOrganizationData.add(activeOrgName);

    final VTextField activeOrgCity = new VTextField(getTranslation("organization.city")).withClassName(StyleConstants.WIDTH_50.getName());
    activeOrganizationBinder.bind(activeOrgCity, "city");
    activeOrganizationData.add(activeOrgCity);

    final VTextField activeOrgStreet = new VTextField(getTranslation("organization.street"));
    activeOrganizationBinder.bind(activeOrgStreet, "street");
    final VTextField activeOrgStreetNumber = new VTextField(getTranslation("organization.streetNumber"));
    activeOrganizationBinder.bind(activeOrgStreetNumber, "streetNumber");
    final VHorizontalLayout streetLayout = new VHorizontalLayout();
    streetLayout.add(activeOrgStreet, activeOrgStreetNumber);
    activeOrganizationData.add(streetLayout);

    final VHorizontalLayout idNumberLayout = new VHorizontalLayout();
    final VTextField activeOrgIDNumber = new VTextField(getTranslation("organization.idNumber"));
    activeOrganizationBinder.bind(activeOrgIDNumber, "identificationNumber");
    idNumberLayout.add(activeOrgIDNumber);
    activeOrganizationData.add(idNumberLayout);

    final VTextField activeOrgIban = new VTextField(getTranslation("organization.iban")).withClassName(StyleConstants.WIDTH_50.getName());
    activeOrganizationBinder.bind(activeOrgIban, "iban");
    activeOrganizationData.add(activeOrgIban);

    final VCheckBox activeOrgActive = new VCheckBox(getTranslation("organization.active"));
    activeOrganizationBinder.bind(activeOrgActive, "active");
    final VDatePicker activeOrgEstablishmentDate = new VDatePicker(getTranslation("organization.establishmentDate"));
    activeOrganizationBinder.bind(activeOrgEstablishmentDate, "establishmentDate");
    final VHorizontalLayout activeLayout = new VHorizontalLayout();
    activeLayout.add(activeOrgEstablishmentDate, activeOrgActive);
    activeOrganizationData.add(activeLayout);

    final VButton saveButton = new VButton(getTranslation("button.save"), e -> {
      try {
        activeOrganizationBinder.writeBean(currentUser.getActiveOrganization().getOrganization());
      } catch (final ValidationException e1) {
      }
      organizationServiceRef.get().saveOrUpdateOrganization(activeOrganizationBinder.getBean());
    });
    activeOrganizationData.add(saveButton);

    final VDiv activeOrganizationRequests = new VDiv().withClassName(StyleConstants.WIDTH_50.getName());
    activeOrganizationRequests.add(new VH5(getTranslation(organizationIsParent == true ? "organization.addPersonsFromChilds" : "organization.requests")));

    if (organizationIsParent && activeOrganization.isActive()) {
      newMembersGrid = initAddPersonsToParentGrid();
      activeOrganizationRequests.add(newMembersGrid);
    }
    if (!organizationIsParent) {
      personOrgRequestsGrid = initActiveRequestsGrid();
      activeOrganizationRequests.add(personOrgRequestsGrid);
    }
    final VHorizontalLayout activeOrganizationLayout = new VHorizontalLayout(activeOrganizationData, activeOrganizationRequests)
      .withClassName(StyleConstants.WIDTH_100.getName());
    add(activeOrganizationLayout);
  }

  /**
   * Fill child members grid.
   */
  private void fillChildMembersGrid() {
    childOrganizationsMembers.clear();
    childOrganizationsMembers.addAll(organizationServiceRef.get().getChildOrganizationMembers(activeOrganization));
    newMembersGrid.setItems(childOrganizationsMembers);
    newMembersGrid.getDataProvider().refreshAll();

  }

  /**
   * Gets the page title.
   *
   * @return the page title
   */
  @Override
  public String getPageTitle() {
    return getTranslation(Routes.getPageTitleKey(Routes.ORGANIZATION));
  }

  /**
   * Inits the active requests grid.
   *
   * @return the v grid
   */
  private VGrid<PersonOrganization> initActiveRequestsGrid() {
    personOrgRequestsGrid = new VGrid<>();
    personOrgRequestsGrid.removeAllColumns();

    personOrgRequestsGrid.addColumn(PersonOrganization::getRequestDate).setHeader(getTranslation("requestGrid.requestDate"));

    personOrgRequestsGrid.addColumn(perOrg -> perOrg.getPerson().getName() + perOrg.getPerson().getLastname()).setHeader(getTranslation("requestGrid.user"));
    personOrgRequestsGrid.addComponentColumn(perOrg -> {
      final VSelect<UserRole> roleSelect = new VSelect<>();
      roleSelect.setItems(Arrays.asList(UserRole.values()));
      roleSelect.setItemLabelGenerator(role -> getTranslation("role." + role.getName().toLowerCase() + ".name"));
      roleSelect.setValue(perOrg.getRole());
      roleSelect.addValueChangeListener(e -> perOrg.setRole(e.getValue()));
      return roleSelect;
    }).setHeader(getTranslation("requestGrid.role"));

    personOrgRequestsGrid.addComponentColumn(perOrg -> {
      final VHorizontalLayout buttonsLayout = new VHorizontalLayout();
      final VButton approveButton = new VButton(getTranslation("button.approve"));
      approveButton.addClickListener(e -> {
        perOrg.setAppRights(true);
        perOrg.setJoinDate(LocalDate.now());
        organizationServiceRef.get().addOrUpdateOrganizationForPerson(perOrg);
        personOrganizationRequests.removeIf(personOrg -> personOrg.getId().equals(perOrg.getId()));
        personOrgRequestsGrid.getDataProvider().refreshAll();
      });
      buttonsLayout.add(approveButton);

      final VButton removeButton = new VButton(getTranslation("button.reject"));
      removeButton.addClickListener(e -> {
        organizationServiceRef.get().removeOrganizationFromPerson(perOrg.getId());
        personOrganizationRequests.removeIf(personOrg -> personOrg.getId().equals(perOrg.getId()));
        personOrgRequestsGrid.getDataProvider().refreshAll();
      });
      buttonsLayout.add(removeButton);
      return buttonsLayout;
    });

    return personOrgRequestsGrid;
  }

  /**
   * Inits the add persons grid.
   *
   * @return the v grid
   */
  private VGrid<Person> initAddPersonsToParentGrid() {
    newMembersGrid = new VGrid<>();
    newMembersGrid.removeAllColumns();

    newMembersGrid.addColumn(Person::getIdentificationNumber).setHeader(getTranslation("requestGrid.identificationNumber"));
    newMembersGrid.addColumn(per -> per.getName().concat(" ").concat(per.getLastname())).setHeader(getTranslation("requestGrid.user"));

    newMembersGrid.addComponentColumn(per -> {
      final List<String> chOrgs = per.getOrgList().stream().map(PersonOrganization::getOrganization).filter(org -> childOrganizationIds.contains(org.getId()))
        .map(Organization::getName)
        .collect(Collectors.toList());
      final VSpan organizations = new VSpan(String.join(",", chOrgs));
      return organizations;
    }).setHeader(getTranslation("requestGrid.organizations"));

    newMembersGrid.addComponentColumn(per -> {
      final VButton approveButton = new VButton(getTranslation("button.add"))
        .withEnabled(!organizationServiceRef.get().isPersonOrganizationMember(per, activeOrganization));
      approveButton.addClickListener(e -> {
        organizationServiceRef.get().joinOrganization(per, activeOrganization);
        fillChildMembersGrid();
      });
      return approveButton;
    });

    return newMembersGrid;
  }

  /**
   * On attach.
   *
   * @param attachEvent the attach event
   */
  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);

    activeOrganizationBinder.readBean(activeOrganization);

    if (organizationIsParent && activeOrganization.isActive()) {
      fillChildMembersGrid();
    }
    if (!organizationIsParent) {
      personOrganizationRequests.clear();
      personOrganizationRequests.addAll(organizationServiceRef.get().getOrganizationJoinRequests(activeOrganization));
      personOrgRequestsGrid.setItems(personOrganizationRequests);
    }
  }
}
