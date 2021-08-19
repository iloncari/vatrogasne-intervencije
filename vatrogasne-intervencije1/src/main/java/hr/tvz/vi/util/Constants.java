/*
 * Constants Constants.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

    COMMANDER("commander"),

    NONE("none");

    /** The name. */
    @Getter
    private final String name;
  }

  /**
   * The Enum EventAction.
   *
   * @author Igor Lončarić (iloncari2@tvz.hr)
   * @since 12:31:48 PM Aug 13, 2021
   */
  public enum EventAction {
    ADDED,

    REMOVED,

    MODIFIED;
  }

  /**
   * Subscribe to push or session events. Defaults to session scope.
   *
   * @author Goran Petanjek (goran.petanjek@optimit.hr)
   * @since 13:32:01 16. tra 2020.
   */
  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface EventSubscriber {

    /**
     * Scope.
     *
     * @return the subscriber scope
     */
    public SubscriberScope scope() default SubscriberScope.SESSION;
  }

  /**
   * The Enum Gender.
   *
   * @author Igor Lončarić (iloncari2@tvz.hr)
   * @since 5:41:51 PM Aug 12, 2021
   */
  @Getter
  @RequiredArgsConstructor
  public enum Gender {
    MALE("male"),

    FEMALE("female"),

    OTHER("other");

    /** The name. */
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

    /** The Constant RESET_PASSWORD. */
    public static final String RESET_PASSWORD = "resetPassword";

    /** The Constant HOME. */
    public static final String HOME = "home";

    /** The Constant ORGANIZATION. */
    public static final String ORGANIZATION = "organization";

    /** The Constant MEMBERS. */
    public static final String MEMBERS = "members";

    /** The Constant MEMBER. */
    public static final String MEMBER = "member";

    /** The Constant ADD_MEMBER. */
    public static final String ADD_MEMBER = "member/add";

    /** The Constant ACCESS_DENIED. */
    public static final String ACCESS_DENIED = "accessDenied";

    /** The Constant NAVIGATION_ERROR. */
    public static final String NAVIGATION_ERROR = "navigationError";

    /** The Constant VECHILES. */
    public static final String VECHILES = "vechiles";

    /** The Constant NEW_VECHILE. */
    public static final String NEW_VECHILE = "vechile/add";

    /** The Constant VECHILE. */
    public static final String VECHILE = "vechile";

    /**
     * Gets the page title key.
     *
     * @param path the path
     * @return the page title key
     */
    public static final String getPageTitleKey(final String path) {
      return new StringBuilder("page.").append(path).append(".title").toString();
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

    /** The width 25. */
    WIDTH_25("width_25"),

    WIDTH_50("width_50"),
    THEME_PRIMARY_ERROR("primary error"),

    /** The theme primary success. */
    THEME_PRIMARY_SUCCESS("primary success"),

    /** The color red. */
    COLOR_RED("red_text");

    /** The name. */
    private final String name;
  }

  public enum SubscriberScope {
    /** The all. */
    ALL,
    /** The push. */
    PUSH,
    /** The session. */
    SESSION
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

    SPECTATOR("spectator"),

    ADMIN("admin");

    @Getter
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

    /** The not usable. */
    NOT_USABLE("notUsable"),

    /** The usable. */
    USABLE("usable");

    /** The name. */
    private final String name;

    /**
     * Gets the label key.
     *
     * @return the label key
     */
    public String getLabelKey() {
      return "vechileType.".concat(name).concat(".label");
    }

  }

  /**
   * The Enum VechileType.
   *
   * @author Igor Lončarić (iloncari2@tvz.hr)
   * @since 9:19:37 PM Aug 16, 2021
   */
  @RequiredArgsConstructor
  public enum VechileType {

    /** The extinguishing and rescuing. */
    EXTINGUISHING_AND_RESCUING("extinguishingAndRescuing"),

    /** The heights rescuing. */
    HEIGHTS_RESCUING("rescuingFromHeight"),

    /** The technical and special equipment. */
    TECHNICAL_AND_SPECIAL_EQUIPMENT("technicalAndSpecialEquipment"),

    /** The ambulance. */
    AMBULANCE("ambulance"),

    /** The danngerous substances equipment. */
    DANNGEROUS_SUBSTANCES_EQUIPMENT("dangerousSubstancesEquipment"),

    /** The commanding. */
    COMMANDING("commanding"),

    /** The transfer firefighters. */
    TRANSFER_FIREFIGHTERS("transferFirefighters"),

    /** The supply. */
    SUPPLY("supply"),

    /** The special. */
    SPECIAL("special"),

    /** The drones. */
    DRONES("drones"),

    /** The vessels. */
    VESSELS("vessels");

    /** The name. */
    private final String name;

    /**
     * Gets the label key.
     *
     * @return the label key
     */
    public String getLabelKey() {
      return "vechileType.".concat(name).concat(".label");
    }
  }
}
