/*
 * OrganizationRepository OrganizationRepository.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.orm;

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
   * Find by username and hashed password.
   *
   * @param username the username
   * @param hashedPassword the hashed password
   * @return the person
   */
  Person findByUsernameAndHashedPassword(String username, String hashedPassword);

}
