/*
 * MainView MainView.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.orm.OrganizationRepository;
import hr.tvz.vi.orm.PersonOrganizationRepository;
import hr.tvz.vi.orm.PersonRepository;
import hr.tvz.vi.util.Utils;
import hr.tvz.vi.view.MainAppLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@Slf4j
@Route(value = "main", layout = MainAppLayout.class)
@PWA(name = "Vaadin Application",
  shortName = "Vaadin App",
  description = "This is an example Vaadin application.",
  enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@Component
public class MainView extends VerticalLayout {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4436379181005364155L;

  @Autowired
  SimpleI18NProvider translationProvider;

  @Autowired
  OrganizationRepository organizationRepository;

  @Autowired
  PersonRepository personRepository;

  @Autowired
  PersonOrganizationRepository personOrgRepo;

  /**
   * Construct a new Vaadin view.
   * <p>
   * Build the initial UI state for the user accessing the application.
   *
   * @param service The message service. Automatically injected Spring managed bean.
   */
  public MainView() {

    // Use TextField for standard text input
    final TextField textField = new TextField("Your name");
    textField.addThemeName("bordered");

    // Button click listeners can be defined as lambda expressions
    final Button button = new Button("Say hello",
      e -> {

        final CurrentUser p = Utils.getCurrentUser(UI.getCurrent());

        Notification.show("Current person: " + p);

      });

    // Theme variants give you predefined extra styles for components.
    // Example: Primary button has a more prominent look.
    button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

    // You can specify keyboard shortcuts for buttons.
    // Example: Pressing enter in this view clicks the Button.
    button.addClickShortcut(Key.ENTER);

    // Use custom CSS classes to apply styling. This is defined in shared-styles.css.
    addClassName("centered-content");

    add(textField, button);

  }

}
