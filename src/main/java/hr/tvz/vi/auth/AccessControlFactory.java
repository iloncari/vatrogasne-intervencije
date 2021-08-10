/*
 * AccessControlFactory AccessControlFactory.java.
 *
 */
package hr.tvz.vi.auth;

import hr.tvz.vi.service.AuthentificationService;

import lombok.RequiredArgsConstructor;

/**
 * A factory for creating AccessControl objects.
 */
@RequiredArgsConstructor(staticName = "of")
public class AccessControlFactory {

  private AccessControl accessControl;

  /**
   * Creates a new AccessControl object.
   *
   * @param authentificationService the authentification service
   * @return the access control
   */
  public AccessControl getAccessControl(AuthentificationService authentificationService) {
    if (null == accessControl) {
      accessControl = AccessControlImpl.getInstance(authentificationService);
    }
    return accessControl;
  }
}
