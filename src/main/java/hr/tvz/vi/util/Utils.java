/*
 * Utils Utils.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.util;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinService;

import hr.tvz.vi.auth.AccessControlImpl;
import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.orm.Person;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE, staticName = "of")
public final class Utils {

  /**
   * Gets the current person.
   *
   * @param ui the ui
   * @return the current person
   */
  public static CurrentUser getCurrentUser(final UI ui) {
    Person person = null;
    if (ui != null) {
      person = ComponentUtil.getData(ui, Person.class);
    }
    if (person == null && VaadinService.getCurrentRequest() != null) {
      return (CurrentUser) VaadinService.getCurrentRequest().getWrappedSession()
        .getAttribute(AccessControlImpl.CURRENT_USER_SESSION_ATTRIBUTE_KEY);
    }
    return null;
  }
}
