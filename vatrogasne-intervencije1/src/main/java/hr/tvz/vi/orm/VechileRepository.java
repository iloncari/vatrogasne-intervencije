/*
 * VechileRepository VechileRepository.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.orm;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VechileRepository extends JpaRepository<Vechile, Long> {

  List<Vechile> findByOrganizationId(Long organizationId);

}
