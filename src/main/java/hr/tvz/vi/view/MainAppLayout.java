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
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import hr.tvz.vi.auth.AccessControlFactory;
import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.orm.PersonOrganization;
import hr.tvz.vi.service.AuthentificationService;
import hr.tvz.vi.util.Constants.ImageConstants;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.select.VSelect;

import de.codecamp.vaadin.serviceref.ServiceRef;

/**
 * The Class MainAppLayout.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 12:24:01 PM Aug 7, 2021
 */
@CssImport("./styles/custom-notification.css")
public class MainAppLayout extends AppLayoutRouterLayout<LeftLayouts.LeftResponsiveHybrid> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1575520009397533185L;

  /** The current user. */
  private final CurrentUser currentUser;

  /** The auth service ref. */
  @Autowired
  private ServiceRef<AuthentificationService> authServiceRef;

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

    final VSelect<PersonOrganization> personOrganizationSelect = new VSelect<>();
    personOrganizationSelect.setItems(currentUser.getCurrentPersonOrganizations());
    personOrganizationSelect.setItemLabelGenerator(personOrg -> personOrg.getOrganization().getName());
    personOrganizationSelect.setValue(currentUser.getActiveOrganization());
    personOrganizationSelect.addValueChangeListener(e -> {
      currentUser.setActiveOrganization(e.getValue());
      UI.getCurrent().getPage().reload();
    });
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

    final LeftNavigationItem newReportsItem = new LeftNavigationItem(getTranslation(Routes.getPageTitleKey(Routes.ORGANIZATION)), VaadinIcon.LIST.create(),
      OrganizationView.class);
    final DefaultBadgeHolder newReportsBadge = new DefaultBadgeHolder(4);
    newReportsBadge.bind(newReportsItem.getBadge());
    newReportsItem.setClickListener(e -> newReportsBadge.decrease());

    leftMenuBuilder.addToSection(Section.DEFAULT, homeItem, newReportsItem);
    return leftMenuBuilder.build();
  }

}
