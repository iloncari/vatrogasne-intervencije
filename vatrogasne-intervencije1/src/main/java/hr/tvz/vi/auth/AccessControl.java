/*
 * AccessControl AccessControl.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */

package hr.tvz.vi.auth;

import java.io.Serializable;

/**
 * The Interface AccessControl.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 12:34:37 PM Aug 8, 2021
 */
public interface AccessControl extends Serializable {

  /**
   * Checks if is user signed in.
   *
   * @return true, if is user signed in
   */
  public abstract boolean isUserSignedIn();

  /**
   * Reset password.
   *
   * @param username the username
   */
  public abstract void resetPassword(String username);

  /**
   * Sign in.
   *
   * @param username the username
   * @param password the password
   * @return the current user
   */
  public abstract CurrentUser signIn(String username, String password);

  /**
   * Sign out.
   *
   * @return true, if successful
   */
  public abstract void signOut();
}
