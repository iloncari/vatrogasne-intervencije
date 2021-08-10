/*
 * OrganizationRepository OrganizationRepository.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.orm;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

  /**
   * Find by identification number.
   *
   * @param identificationNumber the identification number
   * @return the person
   */
  Person findByIdentificationNumber(String identificationNumber);

  /**
   * Find by username.
   *
   * @param username the username
   * @return the person
   */
  Person findByUsername(String username);

  /**
   * Find by username is not null and org list organization id in.
   *
   * @param childIds the child ids
   * @return the list
   */
  List<Person> findByUsernameIsNotNullAndOrgList_ExitDateIsNullAndOrgList_JoinDateIsNotNullAndOrgList_AppRightsTrueAndOrgList_OrganizationIdIn(
    List<Long> childIds);

}
