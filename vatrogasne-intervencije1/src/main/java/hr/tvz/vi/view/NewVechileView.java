/*
 * NewVechileView NewVechileView.java.
 *
 */
package hr.tvz.vi.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

import hr.tvz.vi.components.VechileForm;
import hr.tvz.vi.orm.Vechile;
import hr.tvz.vi.service.VechileService;
import hr.tvz.vi.util.Constants.Routes;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import de.codecamp.vaadin.serviceref.ServiceRef;

@Route(value = Routes.NEW_VECHILE, layout = MainAppLayout.class)
public class NewVechileView extends VVerticalLayout implements HasDynamicTitle {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 768784084257946893L;

  /** The vechile service ref. */
  @Autowired
  ServiceRef<VechileService> vechileServiceRef;

  /**
   * Gets the page title.
   *
   * @return the page title
   */
  @Override
  public String getPageTitle() {
    return getTranslation(Routes.getPageTitleKey(Routes.NEW_VECHILE));
  }

  /**
   * On attach.
   *
   * @param attachEvent the attach event
   */
  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    final Vechile newVechile = new Vechile();
    final VechileForm vechileForm = new VechileForm(newVechile, vechileServiceRef.get(), true);
    add(vechileForm);
  }

}
