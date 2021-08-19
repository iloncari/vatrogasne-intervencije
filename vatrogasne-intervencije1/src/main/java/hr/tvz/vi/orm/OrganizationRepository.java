/*
 * OrganizationRepository OrganizationRepository.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.orm;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

  /**
   * Find by identification number.
   *
   * @param organizationIdentificationNumber the organization identification number
   * @return the optional
   */
  Optional<Organization> findByIdentificationNumber(String organizationIdentificationNumber);

}
