/*
 * OrganizationView OrganizationView.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.orm.OrganizationRepository;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Constants.StyleConstants;
import hr.tvz.vi.util.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.checkbox.VCheckBox;
import org.vaadin.firitin.components.datepicker.VDatePicker;
import org.vaadin.firitin.components.html.VDiv;
import org.vaadin.firitin.components.html.VH3;
import org.vaadin.firitin.components.html.VH5;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VTextField;

@Route(value = Routes.ORGANIZATION, layout = MainAppLayout.class)
public class OrganizationView extends VVerticalLayout implements HasDynamicTitle {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3567099719758726806L;

  /** The current user. */
  private final CurrentUser currentUser = Utils.getCurrentUser(UI.getCurrent());

  /** The active organization binder. */
  private final Binder<Organization> activeOrganizationBinder = new Binder<>(Organization.class);

  @Autowired
  OrganizationRepository organizationRepository;

  /**
   * Instantiates a new organization view.
   */
  public OrganizationView() {

    final VH3 activeOrganizationTitle = new VH3(getTranslation("organization.active.title"));
    add(activeOrganizationTitle);

    final VDiv activeOrganizationData = new VDiv();
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
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      organizationRepository.save(currentUser.getActiveOrganization().getOrganization());

    });
    activeOrganizationData.add(saveButton);

    final VDiv activeOrganizationRequests = new VDiv();
    activeOrganizationRequests.add(new VH5(getTranslation("organization.requests")));

    final VHorizontalLayout activeOrganizationLayout = new VHorizontalLayout(activeOrganizationData, activeOrganizationRequests);

    add(activeOrganizationLayout);

    activeOrganizationBinder.readBean(currentUser.getActiveOrganization().getOrganization());

    if (currentUser.getParentOrganization() != null && currentUser.isMemberOfParentOrganization()) {
      // organization can have parent, but its possible that user is not member of parent organization
    }

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

}
