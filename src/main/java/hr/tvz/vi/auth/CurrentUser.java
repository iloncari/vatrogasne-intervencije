/*
 * CurrentUser CurrentUser.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.auth;

import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.orm.OrganizationRepository;
import hr.tvz.vi.orm.Person;
import hr.tvz.vi.orm.PersonOrganization;
import hr.tvz.vi.util.Constants.UserRole;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CurrentUser {

  @Getter
  private final Person person;

  @Getter
  private PersonOrganization activeOrganization;
  @Getter
  private Organization parentOrganization;

  @Autowired
  OrganizationRepository organizationRepository;

  public CurrentUser(Person person) {
    this.person = person;

    this.activeOrganization = getCurrentPersonOrganizations().stream().filter(po -> po.isAppRights()).findFirst().orElse(null);
    initParentOrganization();
  }

  public PersonOrganization getActiveOrganization() {
    return this.activeOrganization;
  }

  public Set<PersonOrganization> getCurrentPersonOrganizations() {
    return person.getOrgList().stream().filter(po -> po.getExitDate() == null && po.isAppRights()).collect(Collectors.toSet());
  }

  public String getUsername() {
    return this.person.getUsername();
  }

  public boolean hasManagerRole() {
    return UserRole.MANAGER.equals(activeOrganization.getRole());
  }

  /**
   * Inits the parent organization.
   */
  private void initParentOrganization() {
    parentOrganization = person.getOrgList().stream().map(PersonOrganization::getOrganization)
      .filter(organization -> null != activeOrganization.getOrganization().getParentId()
        && activeOrganization.getOrganization().getParentId() == organization.getId().intValue())
      .findAny().orElse(null);
  }

  /**
   * Checks if is member of parent organization.
   *
   * @return true, if is member of parent organization
   */
  public boolean isMemberOfParentOrganization() {
    if (this.parentOrganization == null) {
      return false;
    }
    return getCurrentPersonOrganizations().stream().map(PersonOrganization::getOrganization).collect(Collectors.toList()).contains(this.parentOrganization);
  }

  public void setActiveOrganization(PersonOrganization organization) {
    this.activeOrganization = organization;
    initParentOrganization();

  }
}
