/*
 * LanguageSelect LanguageSelect.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.components;

import com.vaadin.flow.component.UI;

import hr.tvz.vi.SimpleI18NProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The Class LanguageSelect.
 *
 * @author Ime i prezime (ime.prezime@optimit.hr)
 * @since 4:42:56 PM Aug 7, 2021
 */
public class LanguageSelect extends com.vaadin.flow.component.select.Select<String> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5043437674797042582L;

  /** The translation provider. */

  private final SimpleI18NProvider translationProvider = new SimpleI18NProvider();

  /**
   * Instantiates a new language select.
   */
  public LanguageSelect() {
    final Locale currentLocale = translationProvider.getCurrentLocale();
    setItems(getDisplayLanguages(translationProvider.getProvidedLanguages().values()));
    setValue(getTranslation(translationProvider.getProvidedLanguages().get(currentLocale)));

    addValueChangeListener(event -> {
      final String currentLanguageValue = this.getValue();
      final Locale matchingLocale = findMatchingLocale(translationProvider.getProvidedLanguages(), currentLanguageValue);
      translationProvider.setCurrentLocaleOnSession(matchingLocale);
      UI.getCurrent().getPage().reload();
    });
  }

  /**
   * Find matching locale.
   *
   * @param map the map
   * @param value the value
   * @return the locale
   */
  private Locale findMatchingLocale(Map<Locale, String> map, String value) {
    for (final Entry<Locale, String> entry : map.entrySet()) {
      if (getTranslation(entry.getValue()).equalsIgnoreCase(value)) {
        return entry.getKey();
      }
    }
    return translationProvider.getCurrentLocale();
  }

  /**
   * Gets the display languages.
   *
   * @param translationKeys the translation keys
   * @return the display languages
   */
  private Collection<String> getDisplayLanguages(Collection<String> translationKeys) {
    final ArrayList<String> displayLanguages = new ArrayList<>();
    for (final String key : translationKeys) {
      displayLanguages.add(getTranslation(key));
    }
    return displayLanguages;
  }

}
