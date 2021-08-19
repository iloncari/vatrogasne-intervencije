/*
 * AccessControlImpl AccessControlImpl.java.
 *
 */
package hr.tvz.vi.auth;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

import hr.tvz.vi.orm.Person;
import hr.tvz.vi.service.AuthentificationService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class AccessControlImpl.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 8:19:36 PM Aug 10, 2021
 */
@Slf4j
public class AccessControlImpl implements AccessControl {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7978902462530126851L;

  /** The Constant CURRENT_USER_SESSION_ATTRIBUTE_KEY. */
  public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = AccessControlImpl.class.getCanonicalName() + ".CurrentUser";

  /** The Constant USER_SESSION_MAP. */
  private static final Map<String, com.vaadin.flow.server.WrappedSession> USER_SESSION_MAP = new ConcurrentHashMap<>();

  /**
   * Gets the single instance of AccessControlImpl.
   *
   * @param authService the auth service
   * @return single instance of AccessControlImpl
   */
  static synchronized AccessControl getInstance(AuthentificationService authService) {
    return new AccessControlImpl(authService);
  }

  /** The authentification service. */
  private final AuthentificationService authentificationService;

  /**
   * Instantiates a new access control impl.
   *
   * @param authService the auth service
   */
  public AccessControlImpl(AuthentificationService authService) {
    this.authentificationService = authService;
  }

  /**
   * Adds the user to session.
   *
   * @param currentUser the current user
   */
  private void addUserToSession(final CurrentUser currentUser) {
    if (currentUser == null) {
      return;
    }
    getCurrentRequest().getWrappedSession().setAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY, currentUser);
    USER_SESSION_MAP.put(currentUser.getUsername(), getCurrentRequest().getWrappedSession());
    log.info("User {} is successfully logged in from IP: {}.", currentUser.getUsername(), VaadinRequest.getCurrent().getHeader("X-Forwarded-For"));
  }

  private void deleteCurrentUser() {
    final CurrentUser currentUser = getCurrentUser();
    if (null != currentUser) {
      USER_SESSION_MAP.remove(currentUser.getUsername());
      getCurrentRequest().getWrappedSession().removeAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
    }
  }

  /**
   * Gets the current request.
   *
   * @return the current request
   */
  private com.vaadin.flow.server.VaadinRequest getCurrentRequest() {
    return VaadinService.getCurrentRequest();
  }

  /**
   * Gets the current person.
   *
   * @return the current person
   */
  private CurrentUser getCurrentUser() {
    if (null == getCurrentRequest()) {
      return null;
    }
    return (CurrentUser) getCurrentRequest().getWrappedSession().getAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
  }

  /**
   * Checks if is user signed in.
   *
   * @return true, if is user signed in
   */
  @Override
  public boolean isUserSignedIn() {
    return getCurrentUser() != null;
  }

  private void logoutOldUser(final String username) {
    try {
      final com.vaadin.flow.server.WrappedSession session = USER_SESSION_MAP.get(username);
      session.removeAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
      session.invalidate();
    } catch (final IllegalStateException e) {
      // do nothing, remove session from map
    }
    USER_SESSION_MAP.remove(username);
  }

  /**
   * Reset password.
   *
   * @param username the username
   */
  @Override
  public void resetPassword(String username) {

  }

  /**
   * Sign in.
   *
   * @param username the username
   * @param password the password
   * @return the person
   */
  @Override
  public CurrentUser signIn(String username, String password) {
    if (StringUtils.isAnyBlank(username, password)) {
      return null;
    }

    final Person person = authentificationService.login(username, password);
    if (person == null) {
      return null;
    }

    if (USER_SESSION_MAP.containsKey(username)) {
      // remove user from session if is logged in other browser
      logoutOldUser(username);
    }
    final CurrentUser currentUser = new CurrentUser(person);
    addUserToSession(currentUser);
    return currentUser;
  }

  /**
   * Sign out.
   */
  @Override
  public void signOut() {
    deleteCurrentUser();
  }

}
