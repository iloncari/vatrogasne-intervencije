/*
 * VechileForm VechileForm.java.
 *
 */

package hr.tvz.vi.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.data.binder.Binder;

import hr.tvz.vi.converter.DoubleToIntegerConverter;
import hr.tvz.vi.orm.Vechile;
import hr.tvz.vi.service.AbstractService;
import hr.tvz.vi.service.VechileService;
import hr.tvz.vi.util.Constants.StyleConstants;
import hr.tvz.vi.util.Constants.VechileCondition;
import hr.tvz.vi.util.Constants.VechileType;
import hr.tvz.vi.util.Utils;
import hr.tvz.vi.view.VechilesView;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.datepicker.VDatePicker;
import org.vaadin.firitin.components.select.VSelect;
import org.vaadin.firitin.components.textfield.VNumberField;
import org.vaadin.firitin.components.textfield.VTextArea;
import org.vaadin.firitin.components.textfield.VTextField;

/**
 * The Class VechileForm.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 10:01:01 PM Aug 16, 2021
 */
public class VechileForm extends AbstractForm<Vechile> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5528834323333126392L;

  /**
   * Instantiates a new vechile form.
   *
   * @param entity the entity
   * @param service the service
   */
  public VechileForm(Vechile entity, AbstractService<Vechile> service, boolean navigateToParentsView) {
    super(entity, service, navigateToParentsView);
  }

  /**
   * Inits the form.
   */
  @SuppressWarnings("unchecked")
  @Override
  protected void initForm() {
    final CustomFormLayout<Vechile> formLayout = (CustomFormLayout<Vechile>) new CustomFormLayout<>(new Binder<>(Vechile.class), formEntity)
      .withClassName(StyleConstants.WIDTH_100.getName());
    formLayout.setFormTitle("vechileForm.title.label");

    final VSelect<VechileType> typeSelect = new VSelect<>();
    formLayout.setLabel(typeSelect, "vechileForm.field.type");
    formLayout.processBinder(typeSelect, null, null, true, "type");
    typeSelect.setItemLabelGenerator(type -> getTranslation(type.getLabelKey()));
    typeSelect.setItems(Arrays.asList(VechileType.values()));
    final VSelect<VechileCondition> conditionSelect = new VSelect<>();
    formLayout.setLabel(conditionSelect, "vechileForm.field.condition");
    formLayout.processBinder(conditionSelect, null, null, true, "condition");
    conditionSelect.setItemLabelGenerator(condition -> getTranslation(condition.getLabelKey()));
    conditionSelect.setItems(Arrays.asList(VechileCondition.values()));
    formLayout.addTwoColumnItemsLayout(typeSelect, conditionSelect);

    final VTextField makeField = new VTextField();
    formLayout.setLabel(makeField, "vechileForm.field.make");
    formLayout.processBinder(makeField, null, null, false, "make");
    final VTextField modelField = new VTextField().withEnabled(!StringUtils.isBlank(formEntity.getMake()));
    formLayout.setLabel(modelField, "vechileForm.field.model");
    formLayout.processBinder(modelField, null, null, false, "model");
    makeField.addValueChangeListener(e -> modelField.setEnabled(!e.getValue().isBlank()));
    formLayout.addTwoColumnItemsLayout(makeField, modelField);

    final VNumberField modelYear = new VNumberField();
    formLayout.setLabel(modelYear, "vechileForm.field.modelYear");
    formLayout.processBinder(modelYear, new DoubleToIntegerConverter(), null, false, "modelYear");
    final VTextField vechileNumberField = new VTextField();
    formLayout.setLabel(vechileNumberField, "vechileForm.field.vechileNumber");
    formLayout.processBinder(vechileNumberField, null, null, true, "vechileNumber");
    formLayout.addTwoColumnItemsLayout(modelYear, vechileNumberField);

    final VTextArea descField = new VTextArea();
    formLayout.setLabel(descField, "vechileForm.field.description");
    formLayout.processBinder(descField, null, null, false, "description");
    formLayout.addTwoColumnItemsLayout(descField, null);

    final VDatePicker firstRegistrationField = new VDatePicker();
    formLayout.setLabel(firstRegistrationField, "vechileForm.field.firstRegistrationDate");
    formLayout.processBinder(firstRegistrationField, null, null, false, "firstRegistrationDate");
    formLayout.addTwoColumnItemsLayout(firstRegistrationField, null);

    final VTextField licencePlateNumberField = new VTextField();
    formLayout.setLabel(licencePlateNumberField, "vechileForm.field.licencePlateNumber");
    formLayout.processBinder(licencePlateNumberField, null, null, false, "licencePlateNumber");
    final VDatePicker registrationValidUntil = new VDatePicker().withEnabled(StringUtils.isNotBlank(formEntity.getLicencePlateNumber()));
    formLayout.setLabel(registrationValidUntil, "vechileForm.field.registrationValidUntil");
    formLayout.processBinder(registrationValidUntil, null, null, false, "registrationValidUntil");
    licencePlateNumberField.addValueChangeListener(e -> registrationValidUntil.setEnabled(!e.getValue().isBlank()));
    formLayout.addTwoColumnItemsLayout(licencePlateNumberField, registrationValidUntil);

    final VButton saveButton = new VButton(getTranslation("button.save"));
    saveButton.addClickListener(e -> {
      if (formLayout.writeBean()) {
        formEntity.setOrganization(currentUser.getActiveOrganization().getOrganization());
        ((VechileService) entityService).saveOrUpdateVechile(formEntity);
        Utils.showSuccessNotification(2000, Position.TOP_CENTER, "memberForm.notification.memberSaved");
        if (navigateToParentsView) {
          UI.getCurrent().navigate(VechilesView.class);
        }
      }
    });
    formLayout.readBean();

    add(formLayout, saveButton);

  }
}
