/*
 * Utils Utils.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.util;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.server.VaadinService;

import hr.tvz.vi.auth.AccessControlImpl;
import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.orm.Person;
import hr.tvz.vi.util.Constants.StyleConstants;

import org.apache.commons.lang3.StringUtils;

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

  /**
   * Show error notification.
   *
   * @param duration the duration
   * @param position the position
   * @param translationKey the translation key
   * @param translationParams the translation params
   */

  public static void showErrorNotification(final int duration, final Position position, final String translationKey, final Object... translationParams) {
    final Notification notification = new Notification(StringUtils.EMPTY, duration, position);
    notification.setText(notification.getTranslation(translationKey, translationParams));
    notification.addThemeName(StyleConstants.THEME_PRIMARY_ERROR.getName());
    notification.open();
  }

  /**
   * Show success notification.
   *
   * @param duration the duration
   * @param position the position
   * @param translationKey the translation key
   * @param translationParams the translation params
   */
  public static void showSuccessNotification(final int duration, final Position position, final String translationKey, final Object... translationParams) {
    final Notification notification = new Notification(StringUtils.EMPTY, duration, position);
    notification.setText(notification.getTranslation(translationKey, translationParams));
    notification.addThemeName(StyleConstants.THEME_PRIMARY_SUCCESS.getName());
    notification.open();
  }

}
