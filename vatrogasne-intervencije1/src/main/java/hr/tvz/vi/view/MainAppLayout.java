/*
 * MainAppLayout MainAppLayout.java.
 *
 */

package hr.tvz.vi.view;

import com.github.appreciated.app.layout.component.applayout.LeftLayouts;
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.component.menu.left.items.LeftNavigationItem;
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout;
import com.github.appreciated.app.layout.entity.DefaultBadgeHolder;
import com.github.appreciated.app.layout.entity.Section;
import com.google.common.eventbus.Subscribe;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Push;

import hr.tvz.vi.auth.AccessControlFactory;
import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.event.ChangeBroadcaster;
import hr.tvz.vi.event.PersonOrganizationChangedEvent;
import hr.tvz.vi.orm.PersonOrganization;
import hr.tvz.vi.service.AuthentificationService;
import hr.tvz.vi.service.OrganizationService;
import hr.tvz.vi.util.Constants.EventAction;
import hr.tvz.vi.util.Constants.EventSubscriber;
import hr.tvz.vi.util.Constants.ImageConstants;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Constants.SubscriberScope;
import hr.tvz.vi.util.Utils;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.select.VSelect;

import de.codecamp.vaadin.serviceref.ServiceRef;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class MainAppLayout.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 12:24:01 PM Aug 7, 2021
 */
@CssImport("./styles/custom-notification.css")
@Push
@Slf4j
@EventSubscriber(scope = SubscriberScope.ALL)
public class MainAppLayout extends AppLayoutRouterLayout<LeftLayouts.LeftResponsiveHybrid> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1575520009397533185L;

  /** The current user. */
  private final CurrentUser currentUser;

  /** The members item badge. */
  private DefaultBadgeHolder membersItemBadge;

  /** The vechiles item badge. */
  private DefaultBadgeHolder vechilesItemBadge;

  /** The person organization select. */
  private VSelect<PersonOrganization> personOrganizationSelect;

  /** The auth service ref. */
  @Autowired
  private ServiceRef<AuthentificationService> authServiceRef;

  /** The organization service ref. */
  @Autowired
  private ServiceRef<OrganizationService> organizationServiceRef;

  /**
   * Instantiates a new main app layout.
   */
  public MainAppLayout() {
    currentUser = Utils.getCurrentUser(UI.getCurrent());
    init(AppLayoutBuilder.get(LeftLayouts.LeftResponsiveHybrid.class)
      .withIcon(ImageConstants.APP_LOGO.getPath())
      .withAppBar(buildAppBar())
      .withAppMenu(buildLeftMenu())
      .build());
  }

  /**
   * Builds the app bar.
   *
   * @return the component
   */
  private Component buildAppBar() {
    final VHorizontalLayout appBarLayout = new VHorizontalLayout();

    personOrganizationSelect = new VSelect<>();
    initOrganizationsSelect();
    final Icon signOut = VaadinIcon.EXIT.create();
    signOut.addClickListener(e -> {
      AccessControlFactory.of().getAccessControl(authServiceRef.get()).signOut();
      UI.getCurrent().navigate(LoginView.class);
    });

    appBarLayout.add(personOrganizationSelect, VaadinIcon.BELL.create(), signOut);

    return appBarLayout;
  }

  /**
   * Builds the left menu.
   *
   * @return the component
   */
  private Component buildLeftMenu() {
    final LeftAppMenuBuilder leftMenuBuilder = LeftAppMenuBuilder.get();

    final LeftNavigationItem homeItem = new LeftNavigationItem(getTranslation(Routes.getPageTitleKey(Routes.HOME)), VaadinIcon.HOME.create(), HomeView.class);

    final LeftNavigationItem organizationItem = new LeftNavigationItem(getTranslation(Routes.getPageTitleKey(Routes.ORGANIZATION)), VaadinIcon.LIST.create(),
      OrganizationView.class);

    final LeftNavigationItem membersItem = new LeftNavigationItem(getTranslation(Routes.getPageTitleKey(Routes.MEMBERS)), VaadinIcon.LIST.create(),
      MembersView.class);
    membersItemBadge = new DefaultBadgeHolder();
    membersItemBadge.bind(membersItem.getBadge());

    final LeftNavigationItem vechilesItem = new LeftNavigationItem(getTranslation(Routes.getPageTitleKey(Routes.VECHILES)), VaadinIcon.CAR.create(),
      VechilesView.class);
    vechilesItemBadge = new DefaultBadgeHolder();
    vechilesItemBadge.bind(vechilesItem.getBadge());
    leftMenuBuilder.addToSection(Section.DEFAULT, homeItem, organizationItem, membersItem, vechilesItem);
    return leftMenuBuilder.build();
  }

  /**
   * Inits the organizations select.
   */
  private void initOrganizationsSelect() {
    personOrganizationSelect.setItems(currentUser.getAppAvailablePersonOrganizactions());
    personOrganizationSelect.setItemLabelGenerator(personOrg -> personOrg.getOrganization().getName());
    personOrganizationSelect.setValue(currentUser.getActiveOrganization());
    personOrganizationSelect.addValueChangeListener(e -> {
      currentUser.setActiveOrganization(e.getValue());
      UI.getCurrent().getPage().reload();
    });
  }

  /**
   * On attach.
   *
   * @param attachEvent the attach event
   */
  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    ChangeBroadcaster.registerToPushEvents(this);
    setActiveOrganizationMembersBadge();
  }

  /**
   * On detach.
   *
   * @param detachEvent the detach event
   */
  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    ChangeBroadcaster.unregisterFromPushEvents(this);
  }

  /**
   * Person organization changed.
   *
   * @param event the event
   */
  @Subscribe
  public void personOrganizationChanged(PersonOrganizationChangedEvent event) {
    if (event.getPersonOrganization().getPerson().getId().equals(currentUser.getPerson().getId())) {
      if (EventAction.ADDED.equals(event.getAction())) {
        currentUser.getPerson().getOrgList().add(event.getPersonOrganization());
        initOrganizationsSelect();
      } else if (EventAction.MODIFIED.equals(event.getAction())) {
        final Optional<PersonOrganization> modifiedOrg = currentUser.getPerson().getOrgList().stream()
          .filter(po -> po.getId().equals(event.getPersonOrganization().getId())).findAny();
        if (modifiedOrg.isPresent()) {
          modifiedOrg.get().setRole(event.getPersonOrganization().getRole());
          modifiedOrg.get().setDuty(event.getPersonOrganization().getDuty());
          if (null != event.getPersonOrganization().getExitDate()
            || true != event.getPersonOrganization().isAppRights()) {
          }
        }
      }
    }

    if (event.getPersonOrganization().getOrganization().getId().equals(currentUser.getActiveOrganization().getOrganization().getId())) {
      getUI().ifPresent(ui -> ui.access(() -> setActiveOrganizationMembersBadge()));
    }
  }

  /**
   * Sets the active organization members badge.
   */
  private void setActiveOrganizationMembersBadge() {
    final int membersCount = organizationServiceRef.get().getOrganizationMembers(currentUser.getActiveOrganization().getOrganization()).size();
    membersItemBadge.setCount(membersCount);
  }

}
