/*
 * AbstractService AbstractService.java.
 *
 */
package hr.tvz.vi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The Class AbstractService.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @param <T> the generic type
 * @since 10:08:19 PM Aug 16, 2021
 */
public class AbstractService<T> {

  /** The repository. */
  @Autowired
  JpaRepository<T, Long> repository;

}
