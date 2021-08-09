/*
 * LoginView LoginView.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import hr.tvz.vi.MainView;
import hr.tvz.vi.auth.AccessControlFactory;
import hr.tvz.vi.auth.CurrentUser;
import hr.tvz.vi.components.LanguageSelect;
import hr.tvz.vi.orm.FuelConsuption;
import hr.tvz.vi.orm.FuelConsuptionRepository;
import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.orm.OrganizationRepository;
import hr.tvz.vi.orm.Person;
import hr.tvz.vi.orm.PersonOrganization;
import hr.tvz.vi.orm.PersonOrganizationRepository;
import hr.tvz.vi.orm.PersonRepository;
import hr.tvz.vi.orm.ReportRepository;
import hr.tvz.vi.orm.Service;
import hr.tvz.vi.orm.ServiceRepository;
import hr.tvz.vi.orm.TaskRepository;
import hr.tvz.vi.orm.Vechile;
import hr.tvz.vi.orm.VechileRepository;
import hr.tvz.vi.util.Constants.Duty;
import hr.tvz.vi.util.Constants.Professions;
import hr.tvz.vi.util.Constants.Routes;
import hr.tvz.vi.util.Constants.StyleConstants;
import hr.tvz.vi.util.Constants.UserRole;
import hr.tvz.vi.util.Constants.VechileCondition;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.html.VImage;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VPasswordField;
import org.vaadin.firitin.components.textfield.VTextField;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class LoginView.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 2:08:59 PM Aug 7, 2021
 */
@Route(Routes.LOGIN)
@Slf4j
@CssImport("./styles/shared-styles.css")
public class LoginView extends VVerticalLayout implements HasDynamicTitle {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3811165182745711495L;

  /** The person repository. */
  @Autowired
  PersonRepository personRepository;
  @Autowired
  FuelConsuptionRepository fuelConsuptionRepository;
  @Autowired
  ServiceRepository serviceRepository;
  @Autowired
  VechileRepository vechileRepository;
  @Autowired
  PersonOrganizationRepository personOrganizationRepository;
  @Autowired
  OrganizationRepository organizationRepo;
  @Autowired
  TaskRepository taskRepository;
  @Autowired
  ReportRepository reportRepository;

  /**
   * Instantiates a new login view.
   */
  public LoginView() {
    setHeightFull();
    setClassName(StyleConstants.FIRE_GRADIENT.getName());
    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.CENTER);
    add(initLoginForm());

    final Span titleTest = new Span("Samo za test!");
    final TextField operationTextFiedl = new TextField("operacija");
    final VButton reinitButton = new VButton("izvrsi operaciju", e -> {
      log.info(operationTextFiedl.getValue());
      if ("reinit".equals(operationTextFiedl.getValue())) {
        reinit();
      } else if ("show".equals(operationTextFiedl.getValue())) {
        show();
      } else if ("userForLogin".equals(operationTextFiedl.getValue())) {
        userForLogin();
      }
    });
    add(titleTest);
    add(operationTextFiedl);
    add(reinitButton);

  }

  /**
   * Gets the page title.
   *
   * @return the page title
   */
  @Override
  public String getPageTitle() {
    return getTranslation(Routes.getPageTitleKey(Routes.LOGIN));
  }

  /**
   * Inits the login form.
   *
   * @return the component
   */
  private Component initLoginForm() {
    final VVerticalLayout formLayout = new VVerticalLayout();
    formLayout.getElement().getThemeList().removeAll(formLayout.getElement().getThemeList());
    formLayout.setWidth("280px");
    formLayout.setClassName(StyleConstants.LOGIN_CONTENT.getName());

    final VImage logoImage = new VImage(hr.tvz.vi.util.Constants.ImageConstants.APP_LOGO.getPath(), "");
    logoImage.setClassName(StyleConstants.LOGO_CENTER.getName());
    formLayout.add(logoImage);

    final VTextField usernameField = new VTextField(getTranslation("username.label"));
    usernameField.setValue("username34");
    usernameField.addClassNames(StyleConstants.WIDTH_100.getName());
    formLayout.add(usernameField);

    final VPasswordField passwordField = new VPasswordField(getTranslation("password.label"));
    passwordField.addClassNames(StyleConstants.WIDTH_100.getName());
    passwordField.setValue("password34");
    formLayout.add(passwordField);

    final VButton signInButton = new VButton(getTranslation("signIn.label"),
      e -> login(usernameField, passwordField));
    signInButton.addClassNames(StyleConstants.BUTTON_BLUE.getName(), StyleConstants.WIDTH_100.getName());
    formLayout.add(signInButton);

    final RouterLink forgottenPasswordLink = new RouterLink(getTranslation("resetPassword.label"), MainView.class);
    formLayout.add(forgottenPasswordLink);

    final RouterLink requestAccess = new RouterLink(getTranslation("requestAccess.label"), RequestAccessView.class);
    formLayout.add(requestAccess);

    final LanguageSelect languageSelect = new LanguageSelect();
    languageSelect.addClassNames(StyleConstants.WIDTH_100.getName());
    formLayout.add(languageSelect);

    return formLayout;
  }

  /**
   * Login.
   */
  private void login(VTextField username, VPasswordField password) {
    if (StringUtils.isAnyBlank(username.getValue(), password.getValue())) {
      username.setInvalid(username.isEmpty());
      password.setInvalid(password.isEmpty());
      return;
    }

    final CurrentUser currentUser = AccessControlFactory.of().getAccessControl(personRepository).signIn(username.getValue(), password.getValue());
    if (currentUser == null) {
      Notification.show("Krivo korisničko ime i/ili lozinka!");
      username.setInvalid(true);
      password.setInvalid(true);
    } else if (currentUser.getActiveOrganization() == null) {
      Notification.show("Korisnik još nema pristup aplikaciji ili nije član niti jedne organizacije!");
      username.setInvalid(true);
      password.setInvalid(true);
      AccessControlFactory.of().getAccessControl(personRepository).signOut();
    } else {
      UI.getCurrent().navigate(HomeView.class);
      Notification.show("singed: " + AccessControlFactory.of().getAccessControl(personRepository).isUserSignedIn());
    }
  }

  private void reinit() {
    log.info("Deleting tables...");
    fuelConsuptionRepository.deleteAll();
    serviceRepository.deleteAll();
    vechileRepository.deleteAll();

    taskRepository.deleteAll();
    reportRepository.deleteAll();

    personRepository.findAll().forEach(p -> {
      p.setOrgList(null);
      personRepository.save(p);
    });

    personOrganizationRepository.deleteAll();
    personRepository.deleteAll();

    organizationRepo.deleteAll();
    log.info("Tables content deleted!");

    final Random random = new Random();
    log.info("Inserting organizations...");

    for (int i = 1; i <= 50; i++) {
      final Organization organization = new Organization();
      organization.setActive(true);
      organization.setCity("City" + i);
      organization.setEstablishmentDate(LocalDate.of(1936 + i, 1, 1));
      organization.setIban("HR111111" + i);
      organization.setIdentificationNumber("8888888" + i);
      organization.setName("DVD Name" + i);
      organization.setStreet("Ulica" + i);
      organization.setStreetNumber("30" + i);
      organizationRepo.save(organization);
    }
    final List<Organization> orgList = organizationRepo.findAll();
    orgList.forEach(org -> {
      if (random.nextInt(10) < 4) {
        final Organization parent = orgList.get(random.nextInt(orgList.size()));
        if (parent != org) {
          final Set<Organization> childs = new HashSet<>(org.getChilds());
          childs.add(org);
          parent.setChilds(childs);
          organizationRepo.save(parent);
          log.info("Setting parent organization: " + parent.getId() + "(" + parent.getName() + ") -> " + org.getId() + "(" + org.getName() + ")");
        }
      }
    });
    log.info("Organizations inserted!");

    log.info("Inserting vechiles...");
    for (int i = 1; i <= 50; i++) {
      final Vechile vechile1 = new Vechile();
      vechile1.setCondition(Arrays.asList(VechileCondition.values()).get(random.nextInt(Arrays.asList(VechileCondition.values()).size())));
      vechile1.setDescription("Opis vozila" + i);
      vechile1.setFirstRegistrationDate(LocalDate.of(2021 - i, 4, 3));
      vechile1.setLicencePlateNumber("KT111DN" + i);
      vechile1.setMake("Iveco" + i);
      vechile1.setModel("Magirus" + i);
      vechile1.setModelYear(1970 + i);
      vechile1.setOrganization(orgList.get(random.nextInt(orgList.size())));
      vechile1.setVechileNumber("5454543" + i);
      vechile1.setRegistrationValidUntil(LocalDate.now().plusYears(i));
      vechileRepository.save(vechile1);
    }
    log.info("Vechiles inserted!");
    final List<Vechile> vechList = vechileRepository.findAll();

    log.info("Inserting Service...");
    for (int i = 1; i <= 400; i++) {
      final Service s = new Service();
      s.setPrice(Long.valueOf(100 + i));
      s.setServiceDate(LocalDate.now());
      s.setServiceDescription("desc" + i);
      s.setServiceName("name" + i);
      s.setServiceVechile(vechList.get(random.nextInt(vechList.size())));
      serviceRepository.save(s);
    }
    log.info("Service inserted!");
    final List<Service> sList = serviceRepository.findAll();

    log.info("Inserting FuelConsuption...");
    for (int i = 1; i <= 700; i++) {
      final FuelConsuption fc = new FuelConsuption();
      fc.setFillingDate(LocalDate.now());
      fc.setFuelAmount(5 + i);
      fc.setPrice(Long.valueOf(100 + i));
      fc.setFuelVechile(vechList.get(random.nextInt(vechList.size())));
      fuelConsuptionRepository.save(fc);
    }
    log.info("FuelConsuption inserted!");
    final List<FuelConsuption> fcList = fuelConsuptionRepository.findAll();

    log.info("Inserting Person...");
    for (int i = 1; i <= 1000; i++) {
      final boolean appUser = random.nextInt(50) < 7;
      final Person person = new Person();
      person.setBirthDate(LocalDate.now().minusYears(random.nextInt(60)));
      person.setEmail("user" + i + "@tvz.hr");
      person.setIdentificationNumber("1111" + i);
      person.setLastname("lastname" + i);
      person.setName("name" + i);
      person.setProfession(Arrays.asList(Professions.values()).get(random.nextInt(Arrays.asList(Professions.values()).size())));
      if (appUser) {
        person.setHashedPassword("password" + i);
        person.setUsername("username" + i);
      }
      personRepository.save(person);
    }
    log.info("Person inserted!");

    log.info("Inserting PersonOrganizations...");

    final List<Person> personList = personRepository.findAll();
    final List<Organization> orgList1 = organizationRepo.findAll();
    personList.forEach(p1 -> {

      log.info(p1.getName() + " je person");
      final int numberOfPersonOrganizations = random.nextInt(3) + 1;
      log.info("   " + numberOfPersonOrganizations + " organizacije");
      final Set<PersonOrganization> pOSet = new HashSet<>(p1.getOrgList());
      for (int i = 1; i <= numberOfPersonOrganizations; i++) {
        final PersonOrganization pO1 = new PersonOrganization();

        pO1.setDuty(Arrays.asList(Duty.values()).get(random.nextInt(Arrays.asList(Duty.values()).size())));
        pO1.setRole(Arrays.asList(UserRole.values()).get(random.nextInt(Arrays.asList(UserRole.values()).size())));
        pO1.setJoinDate(LocalDate.now());
        final Organization org = orgList1.get(random.nextInt(orgList1.size()));
        log.info(org.getName() + " je organizacija " + i + " " + org.getParentId());
        if (pOSet.stream().map(p -> p.getOrganization().getId()).collect(Collectors.toList()).contains(org.getId())) {
          log.info("Ova organizacija već postoji! Skipamo ju");
          return;
        }
        pO1.setOrganization(org);

        if (org.getParentId() != null && random.nextInt(50) > 10) {
          final Organization parentOrganization = organizationRepo.findById(Long.valueOf(org.getParentId())).orElse(null);
          if (parentOrganization != null) {
            log.info(parentOrganization.getId() + " je organizacija parent!");
            if (pOSet.stream().map(p -> p.getOrganization().getId()).collect(Collectors.toList()).contains(parentOrganization.getId())) {
              log.info("Ova organizacija već postoji! Skipamo ju");
              return;
            }
            final PersonOrganization pO2 = new PersonOrganization();
            pO2.setDuty(Arrays.asList(Duty.values()).get(random.nextInt(Arrays.asList(Duty.values()).size())));
            pO2.setRole(Arrays.asList(UserRole.values()).get(random.nextInt(Arrays.asList(UserRole.values()).size())));
            pO2.setJoinDate(LocalDate.now());
            pO2.setOrganization(parentOrganization);
            pO2.setExitDate(null);
            pO2.setAppRights(pO2.getExitDate() == null && p1.getUsername() != null);
            personOrganizationRepository.save(pO2);
            pOSet.add(pO2);
            log.info("saved parent org");
          }
        }
        pO1.setExitDate(LocalDate.now());
        if (random.nextInt(50) < 10 && !p1.getOrgList().stream().anyMatch(po -> po.getExitDate() == null)) {
          pO1.setExitDate(null);
        }
        pO1.setAppRights(pO1.getExitDate() == null && p1.getUsername() != null);
        personOrganizationRepository.save(pO1);
        log.info("savce po1");
        pOSet.add(pO1);
      }
      p1.setOrgList(pOSet);
      personRepository.save(p1);
    });

    log.info("PersonOrganizations inserted");
  }

  private void show() {

    final List<Person> personList = personRepository.findAll();
    personList.forEach(person -> {
      if (person.getOrgList().isEmpty()) {
        return;
      }
      log.info("Osoba" + (person.getUsername() != null ? "*" : "") + ": " + person.getId() + " " + person.getName());
      person.getOrgList().forEach(po -> {
        log.info("   " + (po.getExitDate() == null ? "*" : "") + po.getId() + " (" + po.getOrganization().getId() + "/"
          + po.getOrganization().getName().replace(" ", "") + ")" + " " + po.getOrganization().getParentId());
      });
    });
  }

  private void userForLogin() {
    personRepository.findAll().stream().filter(person -> {
      if (StringUtils.isAnyBlank(person.getUsername(), person.getHashedPassword())) {
        return false;
      }
      return person.getOrgList().stream().anyMatch(po -> po.getExitDate() == null && po.getOrganization().getParentId() != null);

    }).findAny().ifPresent(p -> Notification.show(p.getUsername() + " " + p.getHashedPassword()));
  }
}
