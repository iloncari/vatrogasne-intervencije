/*
 * AccessControlImpl AccessControlImpl.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.auth;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

import hr.tvz.vi.orm.Person;
import hr.tvz.vi.orm.PersonRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

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
   * @return single instance of AccessControlImpl
   */
  static synchronized AccessControl getInstance(PersonRepository personRepository) {
    return new AccessControlImpl(personRepository);
  }

  PersonRepository personRepository;

  public AccessControlImpl(PersonRepository personRepo) {
    this.personRepository = personRepo;
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
   * Sign in.
   *
   * @param username the username
   * @param password the password
   * @return the person
   */
  @Override
  public CurrentUser signIn(String username, String password) {
    if (StringUtils.isAnyBlank(username, password) || personRepository == null) {
      return null;
    }

    // user hash
    final Person person = this.personRepository.findByUsernameAndHashedPassword(username, password);
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
