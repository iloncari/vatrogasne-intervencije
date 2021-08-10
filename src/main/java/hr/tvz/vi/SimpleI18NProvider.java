/*
 * SimpleI18NProvider SimpleI18NProvider.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.i18n.I18NProvider;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimpleI18NProvider implements I18NProvider {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6792002677914177067L;

  public Locale getCurrentLocale() {
    final UI currentUi = UI.getCurrent();
    return (Locale) Optional.ofNullable(UI.getCurrent().getSession().getAttribute(SimpleI18NProvider.class.getCanonicalName()))
      .orElse(getProvidedLocales().get(0));
    // return Optional.ofNullable(currentUi).map(UI::getLocale).orElse(new Locale("ui"));
  }

  /**
   * Gets the provided languages.
   *
   * @return the provided languages
   */
  public Map<Locale, String> getProvidedLanguages() {
    final HashMap<Locale, String> languages = new HashMap<>();
    for (final Locale locale : getProvidedLocales()) {
      final StringBuilder localizationPrefixBuilder = new StringBuilder();
      localizationPrefixBuilder.append("locale.language.name.").append(locale.getDisplayLanguage().toLowerCase());
      languages.put(locale, localizationPrefixBuilder.toString());
    }
    return languages;
  }

  /**
   * Gets the provided locales.
   *
   * @return the provided locales
   */
  @Override
  public List<Locale> getProvidedLocales() {
    return Collections.unmodifiableList(
      Arrays.asList(new Locale("hr", "HR"), new Locale("en", "US")));
  }

  /**
   * Gets the translation.
   *
   * @param key the key
   * @param locale the locale
   * @param params the params
   * @return the translation
   */
  @Override
  public String getTranslation(String key, Locale locale, Object... params) {
    if (key == null) {
      LoggerFactory.getLogger(SimpleI18NProvider.class.getName())
        .warn("Got lang request for key with null value!");
      return "";
    }

    final ResourceBundle bundle = ResourceBundle.getBundle("translation", getCurrentLocale());
    if (bundle == null) {
      return "bundle null";
    }

    String value;
    try {
      value = bundle.getString(key);
    } catch (final MissingResourceException e) {
      // LoggerFactory.getLogger(SimpleI18NProvider.class.getName())
      // .warn("Missing resource", e);
      return "!" + locale.getLanguage() + ": " + key;
    }
    if (params.length > 0) {
      value = MessageFormat.format(value, params);
    }
    return value;
  }

  public void setCurrentLocaleOnSession(final Locale locale) {
    final UI currentUi = UI.getCurrent();
    currentUi.getSession().setAttribute(SimpleI18NProvider.class.getCanonicalName(), locale);
  }

}
