/*
 * AccessControlFactory AccessControlFactory.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */

package hr.tvz.vi.auth;

import hr.tvz.vi.orm.PersonRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class AccessControlFactory {

  private AccessControl accessControl;

  /**
   * Creates a new AccessControl object.
   *
   * @return the access control
   */
  public AccessControl getAccessControl(PersonRepository pr) {
    if (null == accessControl) {
      accessControl = AccessControlImpl.getInstance(pr);
    }

    return accessControl;
  }
}
