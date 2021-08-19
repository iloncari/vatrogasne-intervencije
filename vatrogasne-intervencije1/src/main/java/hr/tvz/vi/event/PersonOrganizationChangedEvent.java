/*
 * PersonOrganizationChangedEvent PersonOrganizationChangedEvent.java.
 *
 */
package hr.tvz.vi.event;

import hr.tvz.vi.orm.PersonOrganization;
import hr.tvz.vi.util.Constants.EventAction;

import java.util.EventObject;

import lombok.Getter;

/**
 * The Class PersonOrganizationChangedEvent.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 12:27:04 PM Aug 13, 2021
 */
public class PersonOrganizationChangedEvent extends EventObject {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2507568832485176746L;

  /** The person organization. */
  @Getter
  private final PersonOrganization personOrganization;

  /** The action. */
  @Getter
  private final EventAction action;

  /**
   * Instantiates a new person organization changed event.
   *
   * @param source the source
   * @param personOrganization the person organization
   * @param action the action
   */
  public PersonOrganizationChangedEvent(Object source, PersonOrganization personOrganization, EventAction action) {
    super(source);
    this.personOrganization = personOrganization;
    this.action = action;
  }

}
