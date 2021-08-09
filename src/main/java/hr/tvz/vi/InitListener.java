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
import hr.tvz.vi.orm.PersonRepository;
import hr.tvz.vi.view.LoginView;
import hr.tvz.vi.view.RequestAccessView;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InitListener implements VaadinServiceInitListener {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5853480673151872556L;

  private final Logger log = LoggerFactory.getLogger(InitListener.class);

  @Autowired
  PersonRepository partyRepository;

  @Override
  public void serviceInit(ServiceInitEvent event) {
    final AccessControl accessControl = AccessControlFactory.of().getAccessControl(partyRepository);
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
          && !beforeLeaveEvent.getNavigationTarget().equals(RequestAccessView.class)) {
          beforeLeaveEvent.forwardTo(LoginView.class);
          return;
        }
      });

    });

  }
}
