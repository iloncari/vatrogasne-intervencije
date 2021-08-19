/*
 * InitListener InitListener.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;

import hr.tvz.vi.auth.AccessControl;
import hr.tvz.vi.auth.AccessControlFactory;
import hr.tvz.vi.auth.AccessControlImpl;
import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.service.AuthentificationService;
import hr.tvz.vi.view.LoginView;
import hr.tvz.vi.view.ResetPasswordView;
import hr.tvz.vi.view.TestView;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.codecamp.vaadin.serviceref.ServiceRef;

/**
 * The listener interface for receiving init events.
 * The class that is interested in processing a init
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addInitListener<code> method. When
 * the init event occurs, that object's appropriate
 * method is invoked.
 *
 * @see InitEvent
 */
@Service
public class InitListener implements VaadinServiceInitListener {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5853480673151872556L;

  /** The auth service ref. */
  @Autowired
  private ServiceRef<AuthentificationService> authServiceRef;

  /** The access control. */
  private AccessControl accessControl;

  /**
   * Service init.
   *
   * @param event the event
   */
  @Override
  public void serviceInit(ServiceInitEvent event) {
    accessControl = AccessControlFactory.of().getAccessControl(authServiceRef == null ? null : authServiceRef.get());

    event.getSource().addSessionInitListener(sessionInit -> {
      sessionInit.getSession().setLocale(sessionInit.getSource().getInstantiator().getI18NProvider().getProvidedLocales().get(0));
    });

    event.getSource().addUIInitListener(uiEvent -> {
      if (accessControl.isUserSignedIn()) {
        ComponentUtil.setData(uiEvent.getUI(), CurrentUser.class,
          (CurrentUser) VaadinService.getCurrentRequest().getWrappedSession().getAttribute(AccessControlImpl.CURRENT_USER_SESSION_ATTRIBUTE_KEY));
      }
      final Locale activeLocale = (Locale) uiEvent.getUI().getSession().getAttribute(SimpleI18NProvider.class.getCanonicalName());
      if (null != activeLocale) {
        uiEvent.getUI().setLocale(activeLocale);
      }

      uiEvent.getUI().addBeforeLeaveListener(beforeLeaveEvent -> {
        if (!accessControl.isUserSignedIn() && !beforeLeaveEvent.getNavigationTarget().equals(LoginView.class)
          && !beforeLeaveEvent.getNavigationTarget().equals(TestView.class) && !beforeLeaveEvent.getNavigationTarget().equals(ResetPasswordView.class)) {
          beforeLeaveEvent.forwardTo(LoginView.class);
          return;
        }
      });

    });
  }
}
