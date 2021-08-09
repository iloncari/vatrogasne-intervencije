/*
 * Constants Constants.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * The Class Constants.
 *
 * @author Igor Lončarić (iloncari2@optimit.hr)
 * @since 2:12:27 PM Aug 7, 2021
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

  @RequiredArgsConstructor
  public enum Duty {

    PRESIDENT("president"),

    SECRETARY("secretary"),

    NONE("none");

    private final String name;
  }

  /**
   * The Enum ImageConstants.
   *
   * @author Igor Lončarić (iloncari2@optimit.hr)
   * @since 3:12:45 PM Aug 7, 2021
   */
  @Getter
  @RequiredArgsConstructor
  public enum ImageConstants {

    /** The app logo. */
    APP_LOGO("app_logo.png");

    /** The src. */
    private final String name;

    public String getPath() {
      return "img/" + name;
    }
  }

  /**
  * The Enum Professions.
  *
  * @author Igor Lončarić (iloncari2@tvz.hr)
  * @since 2:12:30 PM Aug 7, 2021
  */
  @Getter
  @RequiredArgsConstructor
  public enum Professions {

    /** The youth firefighter. */
    YOUTH_FIREFIGHTER("youth_firefighter"),

    /** The firefighter. */
    FIREFIGHTER("firefigter"),

    /** The first class firefighter. */
    FIRST_CLASS_FIREFIGHTER("first_class_firefigter"),

    /** The subofficer. */
    SUBOFFICER("subofficer"),

    /** The first class subofficer. */
    FIRST_CLASS_SUBOFFICER("first_class_subofficer"),

    /** The officer. */
    OFFICER("officer"),

    /** The first class officer. */
    FIRST_CLASS_OFFICER("first_class_officer"),

    /** The higher officer. */
    HIGHER_OFFICER("higher_officer"),

    /** The first class higher officer. */
    FIRST_CLASS_HIGHER_OFFICER("fitst_class_higher_officer"),

    /** The other. */
    OTHER("other");

    /** The name. */
    private final String name;

    /**
     * Gets the translation key.
     *
     * @return the translation key
     */
    public String getProfessionTranslationKey() {
      return "profession.".concat(name).concat(".label");
    }
  }

  /**
    * The Enum ReportStatus.
    *
    * @author Igor Lončarić (iloncari2@tvz.hr)
    * @since 11:49:01 AM Aug 9, 2021
    */
  @Getter
  @RequiredArgsConstructor
  public enum ReportStatus {
    NEW("new"),

    APPROVED("approved");

    private final String name;
  }

  /**
   * The Class Routes.
   *
   * @author Igor Lončarić (iloncari2@tvz.hr)
   * @since 5:17:38 PM Aug 7, 2021
   */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Getter
  public static final class Routes {

    /** The Constant LOGIN. */
    public static final String LOGIN = "login";

    /** The Constant REQUEST_ACCESS. */
    public static final String REQUEST_ACCESS = "requestAccess";

    /** The Constant HOME. */
    public static final String HOME = "home";

    /** The Constant ORGANIZATION. */
    public static final String ORGANIZATION = "organization";

    /**
     * Gets the page title key.
     *
     * @param path the path
     * @return the page title key
     */
    public static final String getPageTitleKey(final String path) {
      return new StringBuilder("page.title.").append(path).toString();
    }
  }

  /**
   * The Enum StyleConstants.
   *
   * @author Igor Lončarić (iloncari2@tvz.hr)
   * @since 2:12:30 PM Aug 7, 2021
   */
  @Getter
  @RequiredArgsConstructor
  public enum StyleConstants {

    /** The fire gradient. */
    FIRE_GRADIENT("fire-gradient"),

    /** The login content. */
    LOGIN_CONTENT("login_content"),

    /** The register content. */
    REGISTER_CONTENT("register_content"),

    /** The logo center. */
    LOGO_CENTER("logo_center"),

    /** The button blue. */
    BUTTON_BLUE("button_blue"),

    /** The width 100. */
    WIDTH_100("width_100"),

    /** The width 75. */
    WIDTH_75("width_75"),

    WIDTH_50("width_50");

    /** The name. */
    private final String name;
  }

  /**
   * The Class ThemeAttribute.
   *
   * @author Igor Lončarić (iloncari2@optimit.hr)
   * @since 4:18:17 PM Aug 7, 2021
   */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public final class ThemeAttribute {

    /** The Constant BUTTON_BLUE. */
    public static final String BUTTON_BLUE = "button blue";
  }

  @RequiredArgsConstructor
  public enum UserRole {

    MANAGER("manager"),

    SPECTATOR("spectator");

    private final String name;
  }

  /**
   * The Enum VechileCondition.
   *
   * @author Igor Lončarić (iloncari2@tvz.hr)
   * @since 8:49:31 PM Aug 7, 2021
   */
  @RequiredArgsConstructor
  public enum VechileCondition {

    /** The not registered. */
    NOT_REGISTERED("not_registered"),

    /** The not usable. */
    NOT_USABLE("not_usable"),

    /** The sold. */
    SOLD("sold"),

    /** The transfered. */
    TRANSFERED("transfered"),

    /** The usable. */
    USABLE("usable");

    private final String name;

  }
}
