/*
 * NavigationErrorView NavigationErrorView.java.
 *
 */
package hr.tvz.vi.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;

import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Constants.StyleConstants;

import javax.servlet.http.HttpServletResponse;

import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.html.VParagaph;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

/**
 * The Class NavigationErrorView.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 3:21:05 PM Aug 13, 2021
 */
@CssImport("./styles/shared-styles.css")
public class NavigationErrorView extends VVerticalLayout implements HasErrorParameter<NotFoundException>, HasDynamicTitle {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7715867164357519744L;

  /**
   * Instantiates a new navigation error view.
   */
  public NavigationErrorView() {
    setClassName(StyleConstants.FIRE_GRADIENT.getName());
    setAlignItems(Alignment.CENTER);
    setHeightFull();
    setJustifyContentMode(JustifyContentMode.CENTER);
    add(new VParagaph(getTranslation("navigationError.title")));
    add(new VButton(getTranslation(getTranslation("button.redirect"))).withClickListener(e -> UI.getCurrent().navigate(HomeView.class)));

  }

  /**
   * Gets the page title.
   *
   * @return the page title
   */
  @Override
  public String getPageTitle() {
    return getTranslation(Routes.getPageTitleKey(Routes.NAVIGATION_ERROR));
  }

  /**
   * Sets the error parameter.
   *
   * @param event the event
   * @param parameter the parameter
   * @return the int
   */
  @Override
  public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
    return HttpServletResponse.SC_NOT_FOUND;
  }

}
