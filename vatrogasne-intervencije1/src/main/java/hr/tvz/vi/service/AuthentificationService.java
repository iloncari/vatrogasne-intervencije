/*
 * AuthentificationService AuthentificationService.java.
 *
 */
package hr.tvz.vi.service;

import hr.tvz.vi.orm.Person;
import hr.tvz.vi.orm.PersonRepository;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * The Class AuthentificationService.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 8:17:25 PM Aug 10, 2021
 */
@Service
public class AuthentificationService {

  /** The person repository. */
  @Autowired
  PersonRepository personRepository;

  /**
   * Login.
   *
   * @param username the username
   * @param password the password
   * @return the person
   */
  public Person login(String username, String password) {
    if (StringUtils.isAnyBlank(username, password)) {
      return null;
    }

    final Optional<Person> person = personRepository.findByUsername(username);
    if (person.isEmpty()) {
      return null;
    }

    if (BCrypt.checkpw(password, person.get().getHashedPassword())) {
      return person.get();
    }
    return null;
  }

}
