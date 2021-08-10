/*
 * PersonOrganization PersonOrganization.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.orm;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonOrganizationRepository extends JpaRepository<PersonOrganization, Long> {

  List<PersonOrganization> findByJoinDateIsNull();

  List<PersonOrganization> findByOrganizationIdAndJoinDateIsNull(Long id);

  List<PersonOrganization> findByRequestDateIsNull();

}
