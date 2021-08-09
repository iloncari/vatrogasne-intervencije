/*
 * RequestAccessView RequestAccessView.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import hr.tvz.vi.orm.FuelConsuptionRepository;
import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.orm.OrganizationRepository;
import hr.tvz.vi.orm.PersonRepository;
import hr.tvz.vi.orm.ServiceRepository;
import hr.tvz.vi.orm.VechileRepository;
import hr.tvz.vi.util.Constants.Professions;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Constants.StyleConstants;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.datepicker.VDatePicker;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.select.VSelect;
import org.vaadin.firitin.components.textfield.VEmailField;
import org.vaadin.firitin.components.textfield.VPasswordField;
import org.vaadin.firitin.components.textfield.VTextField;

import lombok.extern.slf4j.Slf4j;

@Route(Routes.REQUEST_ACCESS)
@CssImport("./styles/shared-styles.css")
@Slf4j
public class RequestAccessView extends VVerticalLayout implements HasDynamicTitle {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1615225157047390210L;

  /** The organization repository. */
  @Autowired
  private OrganizationRepository organizationRepository;

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  VechileRepository vechileRepository;
  @Autowired
  ServiceRepository serviceRepository;

  @Autowired
  FuelConsuptionRepository fuelConsuptionRepository;
  /** The organization select. */
  private VSelect<Organization> organizationSelect;
  private List<Organization> organizationsList;

  /**
   * Instantiates a new request access view.
   */
  public RequestAccessView() {
    setHeightFull();
    setClassName(StyleConstants.FIRE_GRADIENT.getName());
    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.CENTER);
    add(initRegisterForm());
  }

  /**
   * Gets the page title.
   *
   * @return the page title
   */
  @Override
  public String getPageTitle() {
    return getTranslation(Routes.getPageTitleKey(Routes.REQUEST_ACCESS));
  }

  /**
   * Inits the register form.
   *
   * @return the component
   */
  private Component initRegisterForm() {
    final FormLayout formLayout = new FormLayout();
    formLayout.getElement().getThemeList().removeAll(formLayout.getElement().getThemeList());
    formLayout.setClassName(StyleConstants.REGISTER_CONTENT.getName());

    final VTextField nameField = new VTextField(getTranslation("name.label"));

    final VTextField lastNameField = new VTextField(getTranslation("lastName.label"));

    final VDatePicker birthDate = new VDatePicker(getTranslation("birthDate.label"));
    birthDate.setMax(LocalDate.now().minusYears(5));
    birthDate.setMin(LocalDate.now().minusYears(100));
    final DatePickerI18n calendarLocalization = new DatePickerI18n();
    birthDate.setI18n(calendarLocalization);

    final VTextField identificationNumberField = new VTextField(getTranslation("identificationNumber.label"));
    identificationNumberField.addValueChangeListener(e -> {
      e.getSource().setInvalid(StringUtils.isEmpty(e.getValue()) || !e.getValue().matches("^[0-9]*$") || e.getValue().length() < e.getSource().getMinLength());
    });
    identificationNumberField.setMinLength(4);
    identificationNumberField.setMaxLength(8);
    identificationNumberField.setValueChangeMode(ValueChangeMode.EAGER);

    final VEmailField emailField = new VEmailField(getTranslation("email.label"));
    final VTextField usernameField = new VTextField(getTranslation("username.label"));

    final VPasswordField passwordField = new VPasswordField(getTranslation("password.label"));
    final VPasswordField repeatPasswordField = new VPasswordField(getTranslation("repeatPassword.label"));

    final VSelect<Professions> professionSelect = new VSelect<>(getTranslation("profession.label"));
    professionSelect.setItemLabelGenerator(item -> getTranslation(item.getProfessionTranslationKey()));
    professionSelect.setItems(Arrays.asList(Professions.values()));

    organizationSelect = new VSelect<>(getTranslation("organization.label"));
    organizationSelect.setItemLabelGenerator(item -> item.getName());

    final VButton requestAccessButton = new VButton(getTranslation("requestAccess.label"));
    requestAccessButton.addClassName(StyleConstants.BUTTON_BLUE.getName());

    final RouterLink signInButton = new RouterLink(getTranslation("signIn.label"), LoginView.class);

    formLayout.add(nameField, lastNameField, birthDate, identificationNumberField, emailField, usernameField, passwordField, repeatPasswordField,
      professionSelect, organizationSelect);
    formLayout.setColspan(requestAccessButton, 2);

    formLayout.add(requestAccessButton, signInButton);
    requestAccessButton.addClickListener(e -> {

    });
    return formLayout;
  }

  /**
   * On attach.
   *
   * @param attachEvent the attach event
   */
  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    organizationsList = organizationRepository.findAll().stream().filter(organization -> organization.getChilds().isEmpty()).collect(Collectors.toList());
    organizationSelect.setItems(organizationsList);
  }

}
