/*
 * LoginView LoginView.java.
 *
 */
package hr.tvz.vi.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import hr.tvz.vi.auth.AccessControl;
import hr.tvz.vi.auth.AccessControlFactory;
import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.components.LanguageSelect;
import hr.tvz.vi.service.AuthentificationService;
import hr.tvz.vi.util.Constants;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Constants.StyleConstants;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.html.VDiv;
import org.vaadin.firitin.components.html.VImage;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VPasswordField;
import org.vaadin.firitin.components.textfield.VTextField;

import de.codecamp.vaadin.serviceref.ServiceRef;

/**
 * The Class LoginView.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 7:27:33 PM Aug 10, 2021
 */
@Route(Routes.LOGIN)
@CssImport("./styles/shared-styles.css")
public class LoginView extends VVerticalLayout implements HasDynamicTitle {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3811165182745711495L;

  /** The username field. */
  private VTextField usernameField;

  /** The password field. */
  private VPasswordField passwordField;

  /** The error div. */
  private VDiv errorDiv;

  /** The auth service ref. */
  @Autowired
  private ServiceRef<AuthentificationService> authServiceRef;

  /**
   * Instantiates a new login view.
   */
  public LoginView() {
    setHeightFull();
    setClassName(StyleConstants.FIRE_GRADIENT.getName());
    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.CENTER);
    add(initLoginForm());
  }

  /**
   * Gets the page title.
   *
   * @return the page title
   */
  @Override
  public String getPageTitle() {
    return getTranslation(Routes.getPageTitleKey(Routes.LOGIN));
  }

  /**
   * Inits the login form.
   *
   * @return the component
   */
  private Component initLoginForm() {
    final VVerticalLayout formLayout = new VVerticalLayout();
    formLayout.getElement().getThemeList().removeAll(formLayout.getElement().getThemeList());
    formLayout.setWidth("280px");
    formLayout.setClassName(StyleConstants.LOGIN_CONTENT.getName());

    final VImage logoImage = new VImage(Constants.ImageConstants.APP_LOGO.getPath(), StringUtils.EMPTY);
    logoImage.setClassName(StyleConstants.LOGO_CENTER.getName());
    formLayout.add(logoImage);

    errorDiv = new VDiv();
    errorDiv.setVisible(false);
    errorDiv.setClassName(StyleConstants.COLOR_RED.getName());
    formLayout.add(errorDiv);

    usernameField = new VTextField(getTranslation("loginView.form.field.username.label"));
    usernameField.addValueChangeListener(e -> errorDiv.setVisible(false));
    usernameField.setValueChangeMode(ValueChangeMode.EAGER);
    // usernameField.setValue("iloncari2");
    usernameField.addClassNames(StyleConstants.WIDTH_100.getName());
    formLayout.add(usernameField);

    passwordField = new VPasswordField(getTranslation("loginView.form.field.password.label"));
    passwordField.addValueChangeListener(e -> errorDiv.setVisible(false));
    passwordField.setValueChangeMode(ValueChangeMode.EAGER);
    passwordField.addClassNames(StyleConstants.WIDTH_100.getName());
    // passwordField.setValue("sk8EbXy6");
    formLayout.add(passwordField);

    final VButton signInButton = new VButton(getTranslation("loginView.form.button.signIn.label"),
      e -> login());
    signInButton.addClassNames(StyleConstants.BUTTON_BLUE.getName(), StyleConstants.WIDTH_100.getName());
    formLayout.add(signInButton);

    final RouterLink forgottenPasswordLink = new RouterLink(getTranslation("loginView.form.button.resetPassword.label"), ResetPasswordView.class);
    formLayout.add(forgottenPasswordLink);

    final LanguageSelect languageSelect = new LanguageSelect();
    languageSelect.addClassNames(StyleConstants.WIDTH_100.getName());
    formLayout.add(languageSelect);

    return formLayout;
  }

  /**
   * Login.
   */
  private void login() {
    if (StringUtils.isAnyBlank(usernameField.getValue(), passwordField.getValue())) {
      usernameField.setInvalid(usernameField.isEmpty());
      passwordField.setInvalid(passwordField.isEmpty());
      return;
    }

    final AccessControl accessControl = AccessControlFactory.of().getAccessControl(authServiceRef.get());
    final CurrentUser currentUser = accessControl.signIn(usernameField.getValue(), passwordField.getValue());
    if (currentUser == null) {
      errorDiv.setVisible(true);
      errorDiv.setText(getTranslation("loginView.form.credentials.error.label"));
      usernameField.setInvalid(true);
      passwordField.setInvalid(true);
    } else if (currentUser.getActiveOrganization() == null) {
      errorDiv.setVisible(true);
      errorDiv.setText(getTranslation("loginView.form.organizationsAccess.error.label"));
      usernameField.setInvalid(true);
      passwordField.setInvalid(true);
      accessControl.signOut();
    } else {
      UI.getCurrent().navigate(HomeView.class);
    }
  }
}
