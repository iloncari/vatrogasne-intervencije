/*
 * OrganizationView OrganizationView.java.
 *
 */

package hr.tvz.vi.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.components.CustomFormLayout;
import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.service.OrganizationService;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Constants.StyleConstants;
import hr.tvz.vi.util.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.datepicker.VDatePicker;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VTextField;

import de.codecamp.vaadin.serviceref.ServiceRef;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class OrganizationView.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 10:21:47 PM Aug 10, 2021
 */
@Slf4j
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

  /**
   * Instantiates a new organization view.
   */
  public OrganizationView() {
    currentUser = Utils.getCurrentUser(UI.getCurrent());
    activeOrganization = currentUser.getActiveOrganization().getOrganization();

    final CustomFormLayout<Organization> organizationFormLayout = new CustomFormLayout<>(new Binder<>(Organization.class), activeOrganization);
    organizationFormLayout.setFormTitle("organizationView.activeOrganization.title");

    final VTextField activeOrgName = new VTextField();
    organizationFormLayout.setLabel(activeOrgName, "organizationView.form.field.name");
    organizationFormLayout.processBinder(activeOrgName, null, null, true, "name");
    organizationFormLayout.addTwoColumnItemsLayout(activeOrgName, StyleConstants.WIDTH_75, null, null);

    final VTextField activeOrgCity = new VTextField();
    organizationFormLayout.setLabel(activeOrgCity, "organizationView.form.field.city");
    organizationFormLayout.processBinder(activeOrgCity, null, null, true, "city");
    organizationFormLayout.addTwoColumnItemsLayout(activeOrgCity, null);

    final VTextField activeOrgStreet = new VTextField();
    organizationFormLayout.setLabel(activeOrgStreet, "organizationView.form.field.street");
    organizationFormLayout.processBinder(activeOrgStreet, null, null, true, "street");
    final VTextField activeOrgStreetNumber = new VTextField();
    organizationFormLayout.setLabel(activeOrgStreetNumber, "organizationView.form.field.streetNumber");
    organizationFormLayout.processBinder(activeOrgStreetNumber, null, null, true, "streetNumber");
    organizationFormLayout.addTwoColumnItemsLayout(activeOrgStreet, StyleConstants.WIDTH_75, activeOrgStreetNumber, StyleConstants.WIDTH_25);

    final VTextField activeOrgIDNumber = new VTextField();
    organizationFormLayout.setLabel(activeOrgIDNumber, "organizationView.form.field.identificationNumber");
    organizationFormLayout.processBinder(activeOrgIDNumber, null, null, true, "identificationNumber");
    final VTextField activeOrgIban = new VTextField();
    organizationFormLayout.setLabel(activeOrgIban, "organizationView.form.field.iban");
    organizationFormLayout.processBinder(activeOrgIban, null, null, false, "iban");
    organizationFormLayout.addTwoColumnItemsLayout(activeOrgIDNumber, activeOrgIban);

    final VDatePicker activeOrgEstablishmentDate = new VDatePicker();
    organizationFormLayout.setLabel(activeOrgEstablishmentDate, "organizationView.form.field.establishmentDate");
    organizationFormLayout.processBinder(activeOrgEstablishmentDate, null, null, false, "establishmentDate");
    organizationFormLayout.addTwoColumnItemsLayout(activeOrgEstablishmentDate, null);

    organizationFormLayout.addSaveBeanButton(e -> {
      if (organizationFormLayout.writeBean()) {
        organizationServiceRef.get().saveOrUpdateOrganization(activeOrganization);
        Utils.showSuccessNotification(2000, Position.TOP_CENTER, "organizationView.notification.saveSuccess");
      }
    });

    organizationFormLayout.readBean();
    add(organizationFormLayout);
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
   * On attach.
   *
   * @param attachEvent the attach event
   */
  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    activeOrganizationBinder.readBean(activeOrganization);
  }
}
