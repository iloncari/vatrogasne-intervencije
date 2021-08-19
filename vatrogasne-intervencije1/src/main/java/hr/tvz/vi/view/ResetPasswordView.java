/*
 * ResetPasswordView ResetPasswordView.java.
 *
 */
package hr.tvz.vi.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import hr.tvz.vi.auth.AccessControl;
import hr.tvz.vi.auth.AccessControlFactory;
import hr.tvz.vi.components.LanguageSelect;
import hr.tvz.vi.service.AuthentificationService;
import hr.tvz.vi.util.Constants;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Constants.StyleConstants;
import hr.tvz.vi.util.Utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.html.VImage;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VTextField;

import de.codecamp.vaadin.serviceref.ServiceRef;

/**
 * The Class ResetPasswordView.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 7:27:33 PM Aug 10, 2021
 */
@Route(Routes.RESET_PASSWORD)
@CssImport("./styles/shared-styles.css")
public class ResetPasswordView extends VVerticalLayout implements HasDynamicTitle {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4557464170941661211L;

  /** The username field. */
  private VTextField usernameField;

  /** The auth service ref. */
  @Autowired
  private ServiceRef<AuthentificationService> authServiceRef;

  /**
   * Instantiates a new reset password view.
   */
  public ResetPasswordView() {
    setHeightFull();
    setClassName(StyleConstants.FIRE_GRADIENT.getName());
    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.CENTER);
    add(initResetPasswordForm());
  }

  /**
   * Gets the page title.
   *
   * @return the page title
   */
  @Override
  public String getPageTitle() {
    return getTranslation(Routes.getPageTitleKey(Routes.RESET_PASSWORD));
  }

  /**
   * Inits the reset password form.
   *
   * @return the component
   */
  private Component initResetPasswordForm() {
    final VVerticalLayout formLayout = new VVerticalLayout();
    formLayout.getElement().getThemeList().removeAll(formLayout.getElement().getThemeList());
    formLayout.setWidth("280px");
    formLayout.setClassName(StyleConstants.LOGIN_CONTENT.getName());

    final VImage logoImage = new VImage(Constants.ImageConstants.APP_LOGO.getPath(), "");
    logoImage.setClassName(StyleConstants.LOGO_CENTER.getName());
    formLayout.add(logoImage);

    usernameField = new VTextField(getTranslation("loginView.form.field.username.label"));
    usernameField.setValueChangeMode(ValueChangeMode.EAGER);
    usernameField.addClassNames(StyleConstants.WIDTH_100.getName());
    formLayout.add(usernameField);

    final VButton resetPassButton = new VButton(getTranslation("loginView.form.button.resetPassword.label"),
      e -> resetPassword());
    resetPassButton.addClassNames(StyleConstants.BUTTON_BLUE.getName(), StyleConstants.WIDTH_100.getName());
    formLayout.add(resetPassButton);

    final RouterLink signIn = new RouterLink(getTranslation("loginView.form.button.signIn.label"), LoginView.class);
    formLayout.add(signIn);

    final LanguageSelect languageSelect = new LanguageSelect();
    languageSelect.addClassNames(StyleConstants.WIDTH_100.getName());
    formLayout.add(languageSelect);

    return formLayout;
  }

  /**
   * Reset password.
   */
  private void resetPassword() {
    if (StringUtils.isBlank(usernameField.getValue())) {
      usernameField.setInvalid(usernameField.isEmpty());
      usernameField.setErrorMessage(getTranslation("form.field.username.required.error.label"));
      return;
    }

    final AccessControl accessControl = AccessControlFactory.of().getAccessControl(authServiceRef.get());
    accessControl.resetPassword(usernameField.getValue());
    Utils.showSuccessNotification(3000, Position.TOP_CENTER, "resetPasswordView.notification.resetPasswordEmailSent");
  }
}
