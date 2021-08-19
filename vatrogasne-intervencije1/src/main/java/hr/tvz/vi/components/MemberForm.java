/*
 * MemberForm MemberForm.java.
 *
 */
package hr.tvz.vi.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.orm.Person;
import hr.tvz.vi.service.AbstractService;
import hr.tvz.vi.service.OrganizationService;
import hr.tvz.vi.service.PersonService;
import hr.tvz.vi.util.Constants.Gender;
import hr.tvz.vi.util.Constants.Professions;
import hr.tvz.vi.util.Constants.StyleConstants;
import hr.tvz.vi.util.Constants.UserRole;
import hr.tvz.vi.util.Utils;
import hr.tvz.vi.view.MembersView;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.checkbox.VCheckBox;
import org.vaadin.firitin.components.datepicker.VDatePicker;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.select.VSelect;
import org.vaadin.firitin.components.textfield.VEmailField;
import org.vaadin.firitin.components.textfield.VPasswordField;
import org.vaadin.firitin.components.textfield.VTextField;

/**
 * The Class MemberForm.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 7:18:32 PM Aug 11, 2021
 */
public class MemberForm extends AbstractForm<Person> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 252125676081010394L;

  /** The organization service. */
  private final OrganizationService organizationService;

  /**
   * Instantiates a new member form.
   *
   * @param person the person
   * @param personService the person service
   * @param organizationService the organization service
   */
  public MemberForm(Person person, AbstractService<Person> personService, AbstractService<Organization> organizationService, boolean navigateToParentsView) {
    super(person, personService, navigateToParentsView);
    this.organizationService = (OrganizationService) organizationService;
  }

  /**
   * Inits the form.
   */
  @Override
  @SuppressWarnings("unchecked")
  protected void initForm() {
    final CustomFormLayout<Person> basicDataLayout = (CustomFormLayout<Person>) new CustomFormLayout<>(new Binder<>(Person.class), formEntity)
      .withClassName(StyleConstants.WIDTH_50.getName());
    basicDataLayout.setFormTitle("memberForm.basicData.label");
    final VTextField nameField = new VTextField();
    basicDataLayout.setLabel(nameField, "memberForm.field.name");
    basicDataLayout.processBinder(nameField, null, null, false, "name");
    final VTextField lastnameField = new VTextField();
    basicDataLayout.setLabel(lastnameField, "memberForm.field.lastname");
    basicDataLayout.processBinder(lastnameField, null, null, false, "lastname");
    basicDataLayout.addTwoColumnItemsLayout(nameField, lastnameField);

    final VTextField idNumberField = new VTextField().withReadOnly(true);
    basicDataLayout.setLabel(idNumberField, "memberForm.field.identificationNumber");
    basicDataLayout.processBinder(idNumberField, null, null, false, "identificationNumber");
    final VDatePicker birthDateField = new VDatePicker();
    basicDataLayout.setLabel(birthDateField, "memberForm.field.birthDate");
    basicDataLayout.processBinder(birthDateField, null, null, false, "birthDate");
    basicDataLayout.addTwoColumnItemsLayout(idNumberField, birthDateField);

    final VSelect<Gender> genderSelect = new VSelect<>();
    basicDataLayout.setLabel(genderSelect, "memberForm.field.gender");
    genderSelect.setItemLabelGenerator(gender -> getTranslation("gender." + gender.getName() + ".label"));
    genderSelect.setItems(Arrays.asList(Gender.values()));
    basicDataLayout.processBinder(genderSelect, null, null, false, "gender");
    final VSelect<Professions> professionSelect = new VSelect<>();
    basicDataLayout.setLabel(professionSelect, "memberForm.field.profession");
    professionSelect.setItemLabelGenerator(profession -> getTranslation("profession." + profession.getName() + ".label"));
    professionSelect.setItems(Arrays.asList(Professions.values()));
    basicDataLayout.processBinder(professionSelect, null, null, false, "profession");
    basicDataLayout.addTwoColumnItemsLayout(genderSelect, professionSelect);

    basicDataLayout.readBean();

    final CustomFormLayout<Person> contactLayout = new CustomFormLayout<>(new Binder<>(Person.class), formEntity);
    contactLayout.setFormTitle("memberForm.contactData.label");
    final VEmailField emailField = new VEmailField();
    contactLayout.setLabel(emailField, "memberForm.field.email");
    contactLayout.processBinder(emailField, null, null, false, "email");
    final VTextField phoneNumberField = new VTextField();
    contactLayout.setLabel(phoneNumberField, "memberForm.field.phoneNumber");
    contactLayout.processBinder(phoneNumberField, null, null, false, "phoneNumber");
    contactLayout.addTwoColumnItemsLayout(emailField, phoneNumberField);

    final VTextField cityField = new VTextField();
    contactLayout.setLabel(cityField, "memberForm.field.residenceCity");
    contactLayout.processBinder(cityField, null, null, false, "residenceCity");
    contactLayout.addTwoColumnItemsLayout(cityField, null);

    final VEmailField streetField = new VEmailField();
    contactLayout.setLabel(streetField, "memberForm.field.residenceStreet");
    contactLayout.processBinder(streetField, null, null, false, "residenceStreet");
    final VTextField steetNumberField = new VTextField();
    contactLayout.setLabel(steetNumberField, "memberForm.field.residenceStreetNumber");
    contactLayout.processBinder(steetNumberField, null, null, false, "residenceStreetNumber");
    contactLayout.addTwoColumnItemsLayout(streetField, steetNumberField);

    contactLayout.readBean();

    final CustomFormLayout<Person> appDataLayout = (CustomFormLayout<Person>) new CustomFormLayout<>(new Binder<>(Person.class), formEntity)
      .withClassName(StyleConstants.WIDTH_50.getName());
    appDataLayout.setFormTitle("memberForm.appData.label");

    final VTextField usernameField = new VTextField();
    appDataLayout.setLabel(usernameField, "memberForm.field.username");
    appDataLayout.processBinder(usernameField, null, null, false, "username");
    final VCheckBox accessRight = new VCheckBox();
    accessRight.setLabelAsHtml(getTranslation("memberForm.field.accessRights"));
    appDataLayout.addTwoColumnItemsLayout(usernameField, accessRight);

    final boolean changePasswordEnabled = currentUser.getPerson().getIdentificationNumber() == formEntity.getIdentificationNumber()
      || UserRole.MANAGER.equals(currentUser.getActiveOrganization().getRole());
    final VPasswordField passwordField = new VPasswordField().withEnabled(changePasswordEnabled);
    appDataLayout.setLabel(passwordField, "memberForm.field.password");
    final VPasswordField repeatPasswordField = new VPasswordField().withEnabled(changePasswordEnabled);
    appDataLayout.setLabel(repeatPasswordField, "memberForm.field.repeatPassword");
    appDataLayout.addTwoColumnItemsLayout(passwordField, repeatPasswordField);
    accessRight.setEnabled(currentUser.hasManagerRole() && StringUtils.isNoneEmpty(passwordField.getValue(), repeatPasswordField.getValue()));
    appDataLayout.readBean();

    final VButton saveButton = new VButton(getTranslation("button.save"));
    saveButton.addClickListener(e -> {
      boolean succes = true;
      succes = basicDataLayout.writeBean();
      succes = appDataLayout.writeBean();
      succes = contactLayout.writeBean();
      if (succes && !repeatPasswordField.isInvalid() && !passwordField.isInvalid()) {
        formEntity
          .setHashedPassword(StringUtils.isNotBlank(repeatPasswordField.getValue()) ? BCrypt.hashpw(repeatPasswordField.getValue(), BCrypt.gensalt()) : null);
        ((PersonService) entityService).saveOrUpdatePerson(formEntity);
        formEntity.getOrgList().stream().filter(po -> po.getOrganization().getId().equals(currentUser.getActiveOrganization().getId())).findFirst()
          .ifPresent(po -> po.setAppRights(accessRight.getValue()));

        if (accessRight.getValue() && !organizationService.isPersonOrganizationMember(formEntity, currentUser.getActiveOrganization().getOrganization())) {
          organizationService.joinOrganization(formEntity, currentUser.getActiveOrganization().getOrganization(), true);
        }
        Utils.showSuccessNotification(2000, Position.TOP_CENTER, "memberForm.notification.memberSaved");
        if (navigateToParentsView) {
          UI.getCurrent().navigate(MembersView.class);
        }
      }
    });

    if (changePasswordEnabled) {
      passwordField.setValueChangeMode(ValueChangeMode.EAGER);
      passwordField.setMinLength(5);
      passwordField.setErrorMessage(getTranslation("memberForm.field.password.minLength.error", passwordField.getMinLength()));
      passwordField.addValueChangeListener(e -> {
        passwordField.setInvalid(false);
        if (e.getValue().length() < e.getSource().getMinLength()) {
          passwordField.setInvalid(true);
          passwordField.setErrorMessage(getTranslation("memberForm.field.password.minLength.error", passwordField.getMinLength()));
        } else if (!StringUtils.equals(passwordField.getValue(), repeatPasswordField.getValue()) && StringUtils.isNotBlank(repeatPasswordField.getValue())) {
          passwordField.setInvalid(true);
          repeatPasswordField.setErrorMessage(getTranslation("memberForm.field.password.notMatch.error"));
        }
        accessRight.setEnabled(currentUser.hasManagerRole() && StringUtils.isNoneEmpty(passwordField.getValue(), repeatPasswordField.getValue())
          && !passwordField.isInvalid() && !repeatPasswordField.isInvalid());
        saveButton.setEnabled(!passwordField.isInvalid() && !repeatPasswordField.isInvalid());
      });

      repeatPasswordField.setValueChangeMode(ValueChangeMode.EAGER);
      repeatPasswordField.setErrorMessage(getTranslation("memberForm.field.password.notMatch.error"));
      repeatPasswordField.addValueChangeListener(e -> {
        repeatPasswordField.setInvalid(false);
        if (!StringUtils.equals(passwordField.getValue(), repeatPasswordField.getValue()) && StringUtils.isNotBlank(passwordField.getValue())) {
          repeatPasswordField.setErrorMessage(getTranslation("memberForm.field.password.notMatch.error"));
          repeatPasswordField.setInvalid(true);
        }

        accessRight.setEnabled(currentUser.hasManagerRole() && StringUtils.isNoneEmpty(passwordField.getValue(), repeatPasswordField.getValue())
          && !passwordField.isInvalid() && !repeatPasswordField.isInvalid());
        saveButton.setEnabled(!passwordField.isInvalid() && !repeatPasswordField.isInvalid());

      });
    }

    final VHorizontalLayout userDataAppDataLayout = new VHorizontalLayout().withClassName(StyleConstants.WIDTH_100.getName());
    userDataAppDataLayout.add(basicDataLayout, appDataLayout);
    add(userDataAppDataLayout, contactLayout, saveButton);
  }
}
