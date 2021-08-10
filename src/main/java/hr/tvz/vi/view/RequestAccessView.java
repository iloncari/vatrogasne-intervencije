/*
 * RequestAccessView RequestAccessView.java.
 *
 */
package hr.tvz.vi.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.orm.Person;
import hr.tvz.vi.service.OrganizationService;
import hr.tvz.vi.service.PersonService;
import hr.tvz.vi.util.Constants.Professions;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Constants.StyleConstants;
import hr.tvz.vi.util.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.datepicker.VDatePicker;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.select.VSelect;
import org.vaadin.firitin.components.textfield.VEmailField;
import org.vaadin.firitin.components.textfield.VPasswordField;
import org.vaadin.firitin.components.textfield.VTextField;

import de.codecamp.vaadin.serviceref.ServiceRef;

/**
 * The Class RequestAccessView.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 9:34:24 PM Aug 10, 2021
 */
@Route(Routes.REQUEST_ACCESS)
@CssImport("./styles/shared-styles.css")
public class RequestAccessView extends VVerticalLayout implements HasDynamicTitle {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1615225157047390210L;

  /** The name field. */
  private VTextField nameField;

  /** The last name field. */
  private VTextField lastNameField;

  /** The birth date. */
  private VDatePicker birthDate;

  /** The identification number field. */
  private VTextField identificationNumberField;

  /** The email field. */
  private VEmailField emailField;

  /** The username field. */
  private VTextField usernameField;

  /** The password field. */
  private VPasswordField passwordField;

  /** The repeat password field. */
  private VPasswordField repeatPasswordField;

  /** The profession select. */
  private VSelect<Professions> professionSelect;

  /** The organization select. */
  private VSelect<Organization> organizationSelect;

  /** The organizations list. */
  private final List<Organization> organizationsList = new ArrayList<>();

  /** The person service ref. */
  @Autowired
  private ServiceRef<PersonService> personServiceRef;

  /** The organization service ref. */
  @Autowired
  private ServiceRef<OrganizationService> organizationServiceRef;

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
   * @return the form layout
   */
  private FormLayout initRegisterForm() {
    final FormLayout formLayout = new FormLayout();
    formLayout.getElement().getThemeList().removeAll(formLayout.getElement().getThemeList());
    formLayout.setClassName(StyleConstants.REGISTER_CONTENT.getName());

    nameField = new VTextField(getTranslation("name.label"));

    lastNameField = new VTextField(getTranslation("lastName.label"));

    birthDate = new VDatePicker(getTranslation("birthDate.label"));
    birthDate.setMax(LocalDate.now().minusYears(5));
    birthDate.setMin(LocalDate.now().minusYears(100));
    final DatePickerI18n calendarLocalization = new DatePickerI18n();
    birthDate.setI18n(calendarLocalization);

    identificationNumberField = new VTextField(getTranslation("identificationNumber.label"));
    identificationNumberField.addValueChangeListener(e -> {
      if (setErrorIfRequired(e.getSource().getElement(), !e.getValue().matches("^[0-9]*$"), "field.onlyNumbers")) {
        return;
      }
      setErrorIfRequired(e.getSource().getElement(), e.getValue().length() < e.getSource().getMinLength(), "field.minLength", 4);
    });
    identificationNumberField.setMinLength(4);
    identificationNumberField.setMaxLength(8);
    identificationNumberField.setValueChangeMode(ValueChangeMode.EAGER);

    emailField = new VEmailField(getTranslation("email.label"));
    emailField.setValueChangeMode(ValueChangeMode.EAGER);
    emailField.addValueChangeListener(e -> {
      setErrorIfRequired(e.getSource().getElement(), !e.getValue().matches("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$"), "field.notEmail");
    });
    usernameField = new VTextField(getTranslation("username.label"));

    passwordField = new VPasswordField(getTranslation("password.label"));
    repeatPasswordField = new VPasswordField(getTranslation("repeatPassword.label"));
    passwordField.setValueChangeMode(ValueChangeMode.EAGER);
    passwordField.setMinLength(5);
    passwordField.addValueChangeListener(e -> {
      setErrorIfRequired(e.getSource().getElement(), e.getValue().length() < e.getSource().getMinLength(), "field.minLength", 5);
    });

    repeatPasswordField.setValueChangeMode(ValueChangeMode.EAGER);
    repeatPasswordField.addValueChangeListener(e -> {
      setErrorIfRequired(e.getSource().getElement(),
        !e.getValue().equals(passwordField.getValue()) && StringUtils.isNotBlank(passwordField.getValue()), "field.passwordNotMatch");
    });

    professionSelect = new VSelect<>(getTranslation("profession.label"));
    professionSelect.setItemLabelGenerator(item -> getTranslation(item.getProfessionTranslationKey()));
    professionSelect.setItems(Arrays.asList(Professions.values()));

    organizationSelect = new VSelect<>(getTranslation("organization.label"));
    organizationSelect.setItemLabelGenerator(item -> item.getName());

    final VButton requestAccessButton = new VButton(getTranslation("requestAccess.label"));
    requestAccessButton.addClassName(StyleConstants.BUTTON_BLUE.getName());
    requestAccessButton.addClickListener(e -> requestAccess());

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
    organizationsList.addAll(organizationServiceRef.get().getSelectableOrganizations());
    organizationSelect.setItems(organizationsList);
  }

  /**
   * Request access.
   */
  private void requestAccess() {
    boolean error = false;
    error = setErrorIfRequired(nameField.getElement(), StringUtils.isBlank(nameField.getValue()), "field.required") == true ? true : error;
    error = setErrorIfRequired(lastNameField.getElement(), StringUtils.isBlank(lastNameField.getValue()), "field.required") == true ? true : error;
    error = setErrorIfRequired(birthDate.getElement(), birthDate.getValue() == null, "field.required") == true ? true : error;
    error = setErrorIfRequired(identificationNumberField.getElement(), StringUtils.isBlank(identificationNumberField.getValue()), "field.required") == true
      ? true
      : error;
    error = setErrorIfRequired(emailField.getElement(), !emailField.getValue().matches("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$"), "field.notEmail") == true ? true
      : error;
    error = setErrorIfRequired(passwordField.getElement(), StringUtils.isBlank(passwordField.getValue()), "field.required") == true ? true : error;
    error = setErrorIfRequired(repeatPasswordField.getElement(), !repeatPasswordField.getValue().equals(passwordField.getValue()) && !passwordField.isEmpty(),
      "field.passwordNotMatch") == true ? true : error;
    error = setErrorIfRequired(usernameField.getElement(), StringUtils.isBlank(usernameField.getValue()), "field.required") == true ? true : error;
    error = setErrorIfRequired(organizationSelect.getElement(), ObjectUtils.isEmpty(organizationSelect.getValue()), "field.required") == true ? true : error;

    if (error) {
      return;
    }

    final Optional<Person> personOptional = personServiceRef.get().getPerson(identificationNumberField.getValue());
    Person newPerson = personOptional.orElse(null);
    if (personOptional.isPresent() && personOptional.get().getUsername() != null) {
      if (personServiceRef.get().isPersonHaveAccessToAnyOrganization(personOptional.get())) {
        Utils.showSuccessNotification(5000, Position.TOP_CENTER, "requestAccess.notification.userHaveAccess");
        UI.getCurrent().navigate(LoginView.class);
        return;
      } else if (personServiceRef.get().isPersonWaitingForAccess(personOptional.get())) {
        Utils.showSuccessNotification(5000, Position.TOP_CENTER, "requestAccess.notification.requestOnWait");
        UI.getCurrent().navigate(LoginView.class);
        return;
      }
    } else if (personOptional.isEmpty()) {
      newPerson = new Person();
      newPerson.setBirthDate(birthDate.getValue());
      newPerson.setIdentificationNumber(identificationNumberField.getValue());
      newPerson.setLastname(lastNameField.getValue());
      newPerson.setName(nameField.getValue());
      newPerson.setProfession(Optional.ofNullable(professionSelect.getValue()).orElse(Professions.OTHER));
    }

    newPerson.setHashedPassword(BCrypt.hashpw(passwordField.getValue(), BCrypt.gensalt()));
    newPerson.setEmail(emailField.getValue());
    newPerson.setUsername(usernameField.getValue());
    personServiceRef.get().saveOrUpdatePerson(newPerson);
    personServiceRef.get().sendOrganizationJoinRequest(newPerson, organizationSelect.getValue());
    Utils.showSuccessNotification(5000, Position.TOP_CENTER, "requestAccess.notification.requestSent");
    UI.getCurrent().navigate(LoginView.class);

  }

  /**
   * Sets the error if required.
   *
   * @param field the new error if required
   */
  private boolean setErrorIfRequired(Element field, boolean hasValue, String errorKey, Object... translationParams) {
    if (hasValue) {
      field.setProperty("invalid", true);
      field.setProperty("errorMessage", getTranslation(errorKey, translationParams));
      return true;
    } else {
      field.setProperty("invalid", false);
      return false;
    }
  }

}
