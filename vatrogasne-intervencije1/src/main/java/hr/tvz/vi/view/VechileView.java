/*
 * VechileView VechileView.java.
 *
 */

package hr.tvz.vi.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;

import hr.tvz.vi.components.CustomFormLayout;
import hr.tvz.vi.components.VechileForm;
import hr.tvz.vi.event.ChangeBroadcaster;
import hr.tvz.vi.orm.Service;
import hr.tvz.vi.orm.Vechile;
import hr.tvz.vi.service.VechileService;
import hr.tvz.vi.util.Constants.EventSubscriber;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Constants.StyleConstants;
import hr.tvz.vi.util.Constants.SubscriberScope;

import java.time.LocalDate;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.datepicker.VDatePicker;
import org.vaadin.firitin.components.dialog.VDialog;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.components.html.VDiv;
import org.vaadin.firitin.components.html.VH4;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VTextArea;
import org.vaadin.firitin.components.textfield.VTextField;

import de.codecamp.vaadin.serviceref.ServiceRef;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class VechileView.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 6:44:50 PM Aug 18, 2021
 */
@Slf4j
@Route(value = Routes.VECHILE, layout = MainAppLayout.class)
@EventSubscriber(scope = SubscriberScope.PUSH)
public class VechileView extends VVerticalLayout implements HasDynamicTitle, HasUrlParameter<String> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2439319352713321517L;

  /** The vechile. */
  private Vechile vechile;

  /** The services grid. */
  private VGrid<Service> servicesGrid;

  /** The vechile service ref. */
  @Autowired
  private ServiceRef<VechileService> vechileServiceRef;

  /**
   * Gets the page title.
   *
   * @return the page title
   */
  @Override
  public String getPageTitle() {
    return getTranslation(Routes.getPageTitleKey(Routes.VECHILE));
  }

  /**
   * Inits the services grid layout.
   *
   * @return the v vertical layout
   */
  private VVerticalLayout initServicesGridLayout() {
    final VVerticalLayout layout = new VVerticalLayout().withClassName(StyleConstants.WIDTH_100.getName());

    final VH4 serviceLayoutTitle = new VH4(getTranslation("vechileView.servicesGrid.title"));
    layout.add(serviceLayoutTitle);

    servicesGrid = new VGrid<>();
    servicesGrid.setItems(vechile.getServices());
    servicesGrid.addColumn(service -> service.getServiceDate()).setHeader(getTranslation("serviceView.servicesGrid.date"));
    servicesGrid.addColumn(service -> service.getServiceName()).setHeader(getTranslation("serviceView.servicesGrid.name"));
    servicesGrid.addColumn(service -> ObjectUtils.defaultIfNull(service.getPrice(), 0).toString().concat(" kn"))
      .setHeader(getTranslation("serviceView.servicesGrid.price"));
    servicesGrid.addItemClickListener(e -> servicesGrid.setDetailsVisible(e.getItem(),
      StringUtils.isNotBlank(e.getItem().getServiceDescription()) && !servicesGrid.isDetailsVisible(e.getItem())));
    servicesGrid.setItemDetailsRenderer(new ComponentRenderer<>(s -> {
      final VDiv d = new VDiv();
      d.setText(s.getServiceDescription());
      return d;
    }));

    layout.add(servicesGrid);

    final VButton addServiceButton = new VButton(getTranslation("button.add")).withClickListener(e -> showNewServiceDialog());
    layout.add(addServiceButton);

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
    add(new VechileForm(vechile, vechileServiceRef.get(), false));
    add(initServicesGridLayout());
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
   * Sets the parameter.
   *
   * @param event the event
   * @param vechileId the member id
   */
  @Override
  public void setParameter(BeforeEvent event, String vechileId) {
    if (StringUtils.isBlank(vechileId) || !NumberUtils.isParsable(vechileId)) {
      // navigate to NavigationErrorPage
      throw new NotFoundException();
    }

    this.vechile = vechileServiceRef.get().getById(Long.valueOf(vechileId)).orElse(null);
    if (vechile == null) {
      throw new AccessDeniedException("Access Denied");
    }
  }

  /**
   * Show new service dialog.
   */
  private void showNewServiceDialog() {
    final VDialog dialog = new VDialog();
    final VVerticalLayout dialogLayout = new VVerticalLayout();
    final Service service = new Service();
    service.setServiceDate(LocalDate.now());
    final CustomFormLayout<Service> serviceForm = new CustomFormLayout<>(new Binder<>(Service.class), service);
    serviceForm.setFormTitle("vechileView.newServiceDialog.title");
    final VTextField nameField = new VTextField();
    serviceForm.processBinder(nameField, null, null, true, "serviceName");
    serviceForm.addFormItem(nameField, "vechileView.newServiceDialog.field.name");

    final VTextArea descField = new VTextArea();
    serviceForm.processBinder(descField, null, null, false, "serviceDescription");
    serviceForm.addFormItem(descField, "vechileView.newServiceDialog.field.description");

    final VTextField priceField = new VTextField();
    serviceForm.setLabel(priceField, "vechileView.newServiceDialog.field.price");
    serviceForm.processBinder(priceField, new StringToDoubleConverter(getTranslation("form.field.notANumber.error.label")),
      null, false, "price");
    final VDatePicker dateField = new VDatePicker();
    serviceForm.setLabel(dateField, "vechileView.newServiceDialog.field.date");
    serviceForm.processBinder(dateField, null, null, true, "serviceDate");
    serviceForm.addTwoColumnItemsLayout(priceField, dateField);

    serviceForm.addSaveBeanButton(e -> {
      if (serviceForm.writeBean()) {
        vechileServiceRef.get().saveOrUpdateServiceRecord(service, vechile);
        dialog.close();
        UI.getCurrent().getPage().reload();
      }
    });

    serviceForm.readBean();
    dialogLayout.add(serviceForm);

    dialog.add(dialogLayout);
    dialog.open();

  }

}
