/*
 * AbstractForm AbstractForm.java.
 *
 */
package hr.tvz.vi.components;

import com.vaadin.flow.component.UI;

import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.service.AbstractService;
import hr.tvz.vi.util.Utils;

import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

/**
 * The Class AbstractForm.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @param <T> the generic type
 * @since 10:19:52 PM Aug 16, 2021
 */
public abstract class AbstractForm<T> extends VVerticalLayout {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8614333673389247359L;

  /** The form entity. */
  protected final T formEntity;

  /** The current user. */
  protected final CurrentUser currentUser = Utils.getCurrentUser(UI.getCurrent());

  /** The entity service. */
  protected AbstractService<T> entityService;

  /** The navigate to parents view. */
  protected final boolean navigateToParentsView;

  /**
   * Instantiates a new abstract form.
   *
   * @param entity the entity
   * @param service the service
   * @param saveObserver the save observer
   */
  public AbstractForm(T entity, AbstractService<T> service, boolean navigateToParentsView) {
    this.entityService = service;
    this.formEntity = entity;
    this.navigateToParentsView = navigateToParentsView;
    initForm();
  }

  /**
   * Inits the form.
   */
  protected abstract void initForm();

}
