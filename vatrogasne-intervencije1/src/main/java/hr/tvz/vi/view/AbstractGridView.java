/*
 * AbstractGridView AbstractGridView.java.
 *
 */
package hr.tvz.vi.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.HasDynamicTitle;

import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.event.ChangeBroadcaster;
import hr.tvz.vi.util.Constants.EventSubscriber;
import hr.tvz.vi.util.Constants.SubscriberScope;
import hr.tvz.vi.util.Utils;

import java.util.List;

import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.components.html.VH3;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import lombok.Getter;

/**
 * The Class AbstractGridView.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 6:10:55 PM Aug 14, 2021
 */
@EventSubscriber(scope = SubscriberScope.PUSH)
public abstract class AbstractGridView<T> extends VVerticalLayout implements HasDynamicTitle {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8662193361768278434L;

  /** The grid. */
  @Getter
  private final VGrid<T> grid = new VGrid<>();

  /** The current user. */
  @Getter
  private final CurrentUser currentUser = Utils.getCurrentUser(UI.getCurrent());

  /**
   * Instantiates a new abstract grid view.
   */
  public AbstractGridView() {
    add(new VH3(getViewTitle()));
  }

  /**
   * Gets the grid items.
   *
   * @return the grid items
   */
  public abstract List<T> getGridItems();

  /**
   * Gets the page title.
   *
   * @return the page title
   */
  @Override
  public abstract String getPageTitle();

  /**
   * Gets the view title.
   *
   * @return the view title
   */
  protected abstract String getViewTitle();

  /**
   * Inits the bellow button layout.
   *
   * @return the v horizontal layout
   */
  protected abstract VHorizontalLayout initBellowButtonLayout();

  /**
   * Inits the grid layout.
   */
  protected abstract void initGrid();

  /**
   * On attach.
   *
   * @param attachEvent the attach event
   */
  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    ChangeBroadcaster.registerToPushEvents(this);
    initGrid();
    add(grid);
    grid.setItems(getGridItems());
    add(initBellowButtonLayout());
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
   * Sets the grid items.
   */
  protected void setGridItems() {
    grid.setItems(getGridItems());
    grid.getDataProvider().refreshAll();
  }

}
