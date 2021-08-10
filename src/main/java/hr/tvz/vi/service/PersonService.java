/*
 * PersonService PersonService.java.
 *
 */
package hr.tvz.vi.service;

import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.orm.Person;
import hr.tvz.vi.orm.PersonOrganization;
import hr.tvz.vi.orm.PersonOrganizationRepository;
import hr.tvz.vi.orm.PersonRepository;
import hr.tvz.vi.util.Constants.Duty;
import hr.tvz.vi.util.Constants.UserRole;

import java.time.LocalDate;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class PersonService.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 8:07:56 PM Aug 10, 2021
 */
@Service
public class PersonService {

  /** The person repository. */
  @Autowired
  PersonRepository personRepository;

  /** The person organization repository. */
  @Autowired
  PersonOrganizationRepository personOrganizationRepository;

  /**
   * Gets the person.
   *
   * @param idNumber the id number
   * @return the person
   */
  public Optional<Person> getPerson(String idNumber) {
    if (StringUtils.isBlank(idNumber)) {
      return Optional.empty();
    }
    return Optional.ofNullable(personRepository.findByIdentificationNumber(idNumber));
  }

  /**
   * Checks if is person have access to any organization.
   *
   * @param person the person
   * @return true, if is person have access to any organization
   */
  public boolean isPersonHaveAccessToAnyOrganization(Person person) {
    if (person == null) {
      return false;
    }
    return person.getOrgList().stream().anyMatch(personOrg -> personOrg.isAppRights());
  }

  /**
   * Checks if is person waiting for access.
   *
   * @param person the person
   * @return true, if is person waiting for access
   */
  public boolean isPersonWaitingForAccess(Person person) {
    if (person == null) {
      return false;
    }
    return person.getOrgList().stream()
      .anyMatch(personOrg -> personOrg.getJoinDate() == null);
  }

  /**
   * Save or update person.
   *
   * @param person the person
   * @return the person
   */
  public Person saveOrUpdatePerson(Person person) {
    if (person == null) {
      return null;
    }
    return personRepository.save(person);
  }

  /**
   * Send organization join request.
   *
   * @param person the person
   * @param organization the organization
   * @return the person organization
   */
  public PersonOrganization sendOrganizationJoinRequest(Person person, Organization organization) {
    if (person == null || organization == null) {
      return null;
    }

    final PersonOrganization personOrganization = new PersonOrganization();
    personOrganization.setPerson(person);
    personOrganization.setAppRights(false);
    personOrganization.setDuty(Duty.NONE);
    personOrganization.setOrganization(organization);
    personOrganization.setRequestDate(LocalDate.now());
    personOrganization.setRole(UserRole.SPECTATOR);
    return personOrganizationRepository.save(personOrganization);
  }

}
