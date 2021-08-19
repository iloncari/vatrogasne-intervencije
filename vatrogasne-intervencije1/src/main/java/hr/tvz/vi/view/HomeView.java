/*
 * HomeView HomeView.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Utils;

import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = Routes.HOME, layout = MainAppLayout.class)
public class HomeView extends VVerticalLayout implements HasDynamicTitle {

  public HomeView() {
    add(new Span("Naslovna stranica"));
    final CurrentUser cu = Utils.getCurrentUser(UI.getCurrent());
    add(new Span("Prijavljeni korisnik:"));
    add(new Span("   " + cu.getPerson().getName() + " " + cu.getPerson().getEmail()));
    add(new Span("Aplikacijski aktivna organizacija:"));
    add(new Span("   " + cu.getActiveOrganization().getOrganization().getName()));
    add(new Span("   Korisnikova uloga u organizaciji: " + cu.getActiveOrganization().getRole()));
    add(new Span("   Korisnikova dužnost u organizaciji: " + cu.getActiveOrganization().getDuty()));
    add(new Span("Ostale organizacije:"));
    cu.getPerson().getOrgList().forEach(po -> {
      add(new Span("   Naziv: " + po.getOrganization().getName()));
      add(new Span("   Korisnikova uloga u organizaciji: " + po.getRole()));
      add(new Span(po.getExitDate() + " " + po.isAppRights()));
      add(new Span("______________________________________"));
    });
  }

  /**
   * Gets the page title.
   *
   * @return the page title
   */
  @Override
  public String getPageTitle() {
    return getTranslation(Routes.getPageTitleKey(Routes.HOME));
  }

}
