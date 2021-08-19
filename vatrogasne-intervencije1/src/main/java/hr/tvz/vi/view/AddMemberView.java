/*
 * AddMemberView AddMemberView.java.
 *
 */
package hr.tvz.vi.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.components.MemberForm;
import hr.tvz.vi.orm.Person;
import hr.tvz.vi.service.OrganizationService;
import hr.tvz.vi.service.PersonService;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Constants.UserRole;
import hr.tvz.vi.util.Utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import de.codecamp.vaadin.serviceref.ServiceRef;

/**
 * The Class AddMemberView.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 6:42:04 PM Aug 11, 2021
 */
@Route(value = Routes.ADD_MEMBER, layout = MainAppLayout.class)
public class AddMemberView extends VVerticalLayout implements HasDynamicTitle, HasUrlParameter<String> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1670032007088823708L;

  /** The current user. */
  private final CurrentUser currentUser = Utils.getCurrentUser(UI.getCurrent());

  /** The new member id number. */
  private String newMemberIdNumber;

  /** The per service ref. */
  @Autowired
  private ServiceRef<PersonService> perServiceRef;

  /** The organization service ref. */
  @Autowired
  private ServiceRef<OrganizationService> organizationServiceRef;

  /**
   * Gets the page title.
   *
   * @return the page title
   */
  @Override
  public String getPageTitle() {
    return getTranslation(Routes.getPageTitleKey(Routes.ADD_MEMBER));
  }

  /**
   * On attach.
   *
   * @param attachEvent the attach event
   */
  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    final Person newPerson = new Person();
    newPerson.setIdentificationNumber(newMemberIdNumber);
    final MemberForm newMemberForm = new MemberForm(newPerson, perServiceRef.get(), organizationServiceRef.get(), true);
    add(newMemberForm);
  }

  /**
   * Sets the parameter.
   *
   * @param event the event
   * @param memberIdentificationNumber the member identification number
   */
  @Override
  public void setParameter(BeforeEvent event, String memberIdentificationNumber) {
    if (StringUtils.isBlank(memberIdentificationNumber) || !NumberUtils.isParsable(memberIdentificationNumber)) {
      UI.getCurrent().navigate(MembersView.class);
      return;
    }

    if (!UserRole.MANAGER.equals(currentUser.getActiveOrganization().getRole())) {
      throw new AccessDeniedException("Access Denied");
    }

    newMemberIdNumber = memberIdentificationNumber;
  }

}
