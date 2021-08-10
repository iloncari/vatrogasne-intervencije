/*
 * OrganizationService OrganizationService.java.
 *
 */
package hr.tvz.vi.service;

import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.orm.OrganizationRepository;
import hr.tvz.vi.orm.Person;
import hr.tvz.vi.orm.PersonOrganization;
import hr.tvz.vi.orm.PersonOrganizationRepository;
import hr.tvz.vi.orm.PersonRepository;
import hr.tvz.vi.util.Constants.Duty;
import hr.tvz.vi.util.Constants.UserRole;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class OrganizationService.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 10:13:29 PM Aug 10, 2021
 */
@Service
public class OrganizationService {

  /** The organization repository. */
  @Autowired
  private OrganizationRepository organizationRepository;

  /** The person organization repository. */
  @Autowired
  private PersonOrganizationRepository personOrganizationRepository;

  /** The person repository. */
  @Autowired
  private PersonRepository personRepository;

  /**
   * Save or update organization for person.
   *
   * @param personOrganization the person organization
   * @return the person organization
   */
  public PersonOrganization addOrUpdateOrganizationForPerson(PersonOrganization personOrganization) {
    if (personOrganization == null) {
      return null;
    }
    return personOrganizationRepository.save(personOrganization);
  }

  /**
   * Gets the child organization members.
   *
   * @param parentOrganization the parent organization
   * @return the child organization members
   */
  public List<Person> getChildOrganizationMembers(Organization parentOrganization) {
    if (parentOrganization == null || parentOrganization.getChilds().isEmpty()) {
      return new ArrayList<>();
    }

    return personRepository
      .findByUsernameIsNotNullAndOrgList_ExitDateIsNullAndOrgList_JoinDateIsNotNullAndOrgList_AppRightsTrueAndOrgList_OrganizationIdIn(
        parentOrganization.getChilds().stream().map(Organization::getId).collect(Collectors.toList()));
  }

  /**
   * Gets the organization join requests.
   *
   * @param organization the organization
   * @return the organization join requests
   */
  public List<PersonOrganization> getOrganizationJoinRequests(Organization organization) {
    if (organization == null) {
      return new ArrayList<>();
    }
    return personOrganizationRepository
      .findByOrganizationIdAndJoinDateIsNull(organization.getId());
  }

  /**
   * Gets the selectable organizations.
   *
   * @return the selectable organizations
   */
  public List<Organization> getSelectableOrganizations() {
    return organizationRepository.findAll().stream().filter(organization -> organization.getChilds().isEmpty()).collect(Collectors.toList());
  }

  /**
   * Checks if is person organization member.
   *
   * @param person the person
   * @param organization the organization
   * @return true, if is person organization member
   */
  public boolean isPersonOrganizationMember(Person person, Organization organization) {
    if (person == null || organization == null) {
      return false;
    }
    return person.getOrgList().stream().filter(perOrg -> perOrg.getExitDate() == null)
      .anyMatch(perOrg -> perOrg.getOrganization().getId().equals(organization.getId()));

  }

  public PersonOrganization joinOrganization(Person person, Organization organization) {
    if (person == null || organization == null) {
      return null;
    }

    final PersonOrganization personOrganization = new PersonOrganization();
    personOrganization.setPerson(person);
    personOrganization.setAppRights(true);
    personOrganization.setDuty(Duty.NONE);
    personOrganization.setOrganization(organization);
    personOrganization.setRequestDate(LocalDate.now());
    personOrganization.setJoinDate(LocalDate.now());
    personOrganization.setRole(UserRole.SPECTATOR);
    return personOrganizationRepository.save(personOrganization);
  }

  /**
   * Organization is parent.
   *
   * @param organization the organization
   * @return true, if successful
   */
  public boolean organizationIsParent(Organization organization) {
    return !organization.getChilds().isEmpty();
  }

  /**
   * Removes the organization from person.
   *
   * @param organizationId the organization id
   */
  public void removeOrganizationFromPerson(Long organizationId) {
    if (organizationId == null) {
      return;
    }
    personOrganizationRepository.deleteById(organizationId);

  }

  /**
   * Save or update organization.
   *
   * @param organization the organization
   * @return the organization
   */
  public Organization saveOrUpdateOrganization(Organization organization) {
    if (organization == null) {
      return null;
    }
    return organizationRepository.save(organization);
  }

}
