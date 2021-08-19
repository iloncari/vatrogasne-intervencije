/*
 * CurrentUser CurrentUser.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.auth;

import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.orm.Person;
import hr.tvz.vi.orm.PersonOrganization;
import hr.tvz.vi.util.Constants.UserRole;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

/**
 * The Class CurrentUser.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 7:32:11 PM Aug 13, 2021
 */
public class CurrentUser {

  /** The person. */
  @Getter
  private final Person person;

  /** The active organization. */
  @Getter
  private PersonOrganization activeOrganization;

  /** The parent organization. */
  @Getter
  private Organization parentOrganization;

  /**
   * Instantiates a new current user.
   *
   * @param person the person
   */
  public CurrentUser(Person person) {
    this.person = person;
    this.activeOrganization = getAppAvailablePersonOrganizactions().stream().findFirst().orElse(null);
    initParentOrganization();
  }

  /**
   * Gets the active person organizactions.
   *
   * @return the active person organizactions
   */
  public Set<PersonOrganization> getActivePersonOrganizactions() {
    return person.getOrgList().stream().filter(po -> po.getExitDate() == null).collect(Collectors.toSet());
  }

  /**
   * Gets the app available person organizactions.
   *
   * @return the app available person organizactions
   */
  public Set<PersonOrganization> getAppAvailablePersonOrganizactions() {
    return getActivePersonOrganizactions().stream().filter(po -> po.isAppRights()).collect(Collectors.toSet());
  }

  /**
   * Gets the username.
   *
   * @return the username
   */
  public String getUsername() {
    return this.person.getUsername();
  }

  /**
   * Checks for manager role.
   *
   * @return true, if successful
   */
  public boolean hasManagerRole() {
    return UserRole.MANAGER.equals(activeOrganization.getRole());
  }

  /**
   * Inits the parent organization.
   */
  private void initParentOrganization() {
    if (getActiveOrganization() == null) {
      return;
    }
    parentOrganization = getAppAvailablePersonOrganizactions().stream().map(PersonOrganization::getOrganization)
      .filter(organization -> null != activeOrganization.getOrganization().getParentId()
        && activeOrganization.getOrganization().getParentId() == organization.getId().intValue())
      .findAny().orElse(null);
  }

  /**
   * Sets the active organization.
   *
   * @param organization the new active organization
   */
  public void setActiveOrganization(PersonOrganization organization) {
    this.activeOrganization = organization;
    initParentOrganization();

  }
}
