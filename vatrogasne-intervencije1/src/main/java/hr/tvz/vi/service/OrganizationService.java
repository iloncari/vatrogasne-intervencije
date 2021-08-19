/*
 * OrganizationService OrganizationService.java.
 *
 */
package hr.tvz.vi.service;

import hr.tvz.vi.event.ChangeBroadcaster;
import hr.tvz.vi.event.PersonOrganizationChangedEvent;
import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.orm.OrganizationRepository;
import hr.tvz.vi.orm.Person;
import hr.tvz.vi.orm.PersonOrganization;
import hr.tvz.vi.orm.PersonOrganizationRepository;
import hr.tvz.vi.orm.PersonRepository;
import hr.tvz.vi.util.Constants.Duty;
import hr.tvz.vi.util.Constants.EventAction;
import hr.tvz.vi.util.Constants.UserRole;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class OrganizationService.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 10:13:29 PM Aug 10, 2021
 */
@Slf4j
@Service
public class OrganizationService extends AbstractService<Organization> {

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
   * Gets the organization by identification number.
   *
   * @param organizationIdentificationNumber the organization identification number
   * @return the organization by identification number
   */
  public Optional<Organization> getOrganizationByIdentificationNumber(String organizationIdentificationNumber) {
    if (StringUtils.isBlank(organizationIdentificationNumber)) {
      return Optional.empty();
    }
    return ((OrganizationRepository) repository).findByIdentificationNumber(organizationIdentificationNumber);
  }

  /**
   * Gets the organization members.
   *
   * @param organization the organization
   * @return the organization members
   */
  public List<Person> getOrganizationMembers(Organization organization) {
    if (organization == null) {
      return new ArrayList<>();
    }

    return personRepository.findByOrgList_ExitDateIsNullAndOrgList_OrganizationId(organization.getId());
  }

  /**
   * Gets the person organization if member.
   *
   * @param person the person
   * @param organization the organization
   * @return the person organization if member
   */
  public Optional<PersonOrganization> getPersonOrganization(Person person, Organization organization) {
    if (person == null || person == null) {
      return null;
    }
    return person.getOrgList().stream()
      .filter(po -> po.getOrganization().getId().equals(organization.getId())).findFirst();
  }

  /**
   * Gets the selectable organizations.
   *
   * @return the selectable organizations
   */
  public List<Organization> getSelectableOrganizations() {
    return ((OrganizationRepository) repository).findAll().stream().filter(organization -> organization.getChilds().isEmpty()).collect(Collectors.toList());
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

  /**
   * Join organization.
   *
   * @param person the person
   * @param organization the organization
   * @param appAccess the app access
   * @return the person organization
   */
  public PersonOrganization joinOrganization(Person person, Organization organization, boolean appAccess) {
    if (person == null || organization == null) {
      return null;
    }

    final PersonOrganization personOrganization = new PersonOrganization();
    personOrganization.setPerson(person);
    personOrganization.setAppRights(appAccess);
    personOrganization.setDuty(Duty.NONE);
    personOrganization.setOrganization(organization);
    personOrganization.setRequestDate(LocalDate.now());
    personOrganization.setJoinDate(LocalDate.now());
    personOrganization.setRole(UserRole.SPECTATOR);
    final PersonOrganization savedPO = personOrganizationRepository.save(personOrganization);
    ChangeBroadcaster.firePushEvent(new PersonOrganizationChangedEvent(this, savedPO, EventAction.ADDED));
    return savedPO;
  }

  /**
   * Left organization.
   *
   * @param person the person
   * @param organization the organization
   * @return true, if successful
   */
  public boolean leftOrganization(Person person, Organization organization) {
    if (person == null || organization == null) {
      return false;
    }
    final Optional<PersonOrganization> personOrganizationOptional = person.getOrgList().stream()
      .filter(po -> po.getOrganization().getId().equals(organization.getId())).findFirst();
    if (personOrganizationOptional.isPresent()) {
      personOrganizationOptional.get().setAppRights(false);
      personOrganizationOptional.get().setExitDate(LocalDate.now());
      final PersonOrganization po = personOrganizationRepository.save(personOrganizationOptional.get());
      ChangeBroadcaster.firePushEvent(new PersonOrganizationChangedEvent(this, po, EventAction.MODIFIED));
      return true;
    }
    return false;
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
   * Save or update organization.
   *
   * @param organization the organization
   * @return the organization
   */
  public Organization saveOrUpdateOrganization(Organization organization) {
    if (organization == null) {
      return null;
    }
    return ((OrganizationRepository) repository).save(organization);
  }

}
