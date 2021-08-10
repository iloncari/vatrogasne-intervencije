/*
 * TestView TestView.java.
 *
 */
package hr.tvz.vi.view;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

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
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import lombok.extern.slf4j.Slf4j;

@Route(value = "test")
@Slf4j
public class TestView extends VVerticalLayout {

  @Autowired
  FuelConsuptionRepository fuelConsuptionRepository;
  @Autowired
  ServiceRepository serviceRepository;
  @Autowired
  VechileRepository vechileRepository;
  @Autowired
  PersonRepository personRepository;
  @Autowired
  PersonOrganizationRepository personOrganizationRepository;
  @Autowired
  OrganizationRepository organizationRepo;
  @Autowired
  TaskRepository taskRepository;
  @Autowired
  ReportRepository reportRepository;

  public TestView() {
    final TextField operationTextFiedl = new TextField("operacija");
    final VButton reinitButton = new VButton("izvrsi operaciju", e -> {
      log.info(operationTextFiedl.getValue());
      if ("reinit".equals(operationTextFiedl.getValue())) {
        reinit();
      } else if ("show".equals(operationTextFiedl.getValue())) {
        show();
      } else if ("userForLogin".equals(operationTextFiedl.getValue())) {
        userForLogin();
      } else if ("clear".equals(operationTextFiedl.getValue())) {
        clear();
      } else if ("initRealData".equals(operationTextFiedl.getValue())) {
        initRealData();
      }
    });
    add(operationTextFiedl);
    add(reinitButton);
  }

  private void clear() {
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
  }

  private void initRealData() {
    clear();

    log.info("Inserting organizations...");

    final Organization organization1 = new Organization();
    organization1.setActive(true);
    organization1.setCity("Vidrenjak");
    organization1.setEstablishmentDate(LocalDate.of(1936, 1, 1));
    organization1.setIban("HR549375324");
    organization1.setIdentificationNumber("32783207843");
    organization1.setName("DVD Vidrenjak");
    organization1.setStreet("Mije Stuparića");
    organization1.setStreetNumber("30");
    organizationRepo.save(organization1);

    final Organization organization2 = new Organization();
    organization2.setActive(true);
    organization2.setCity("Velika Ludina");
    organization2.setEstablishmentDate(LocalDate.of(1942, 4, 1));
    organization2.setIban("HR932932879732");
    organization2.setIdentificationNumber("43899742487");
    organization2.setName("DVD Velika Ludina");
    organization2.setStreet("Obrtnička");
    organization2.setStreetNumber("12");
    organizationRepo.save(organization2);

    final Organization organization3 = new Organization();
    organization3.setActive(true);
    organization3.setCity("Popovača");
    organization3.setEstablishmentDate(LocalDate.of(1912, 4, 1));
    organization3.setIban("HR89432302342");
    organization3.setIdentificationNumber("3242563464");
    organization3.setName("DVD Popovača");
    organization3.setStreet("Zagrebačka");
    organization3.setStreetNumber("8");
    organizationRepo.save(organization3);

    final Organization organization4 = new Organization();
    organization4.setActive(true);
    organization4.setCity("Vidrenjak");
    organization4.setEstablishmentDate(LocalDate.of(1967, 4, 1));
    organization4.setIban("HR44342353443");
    organization4.setIdentificationNumber("425236543645");
    organization4.setName("VZO Velika Ludina");
    organization4.setStreet("Mije Stuparića");
    organization4.setStreetNumber("30");
    organizationRepo.save(organization4);

    final Set<Organization> childs = new HashSet<>();
    childs.add(organization1);
    childs.add(organization2);
    organization4.setChilds(childs);
    organizationRepo.save(organization4);

    log.info("Organizations inserted!");

    log.info("Inserting Vechile...");
    final Vechile vechile11 = new Vechile();
    vechile11.setCondition(VechileCondition.USABLE);
    vechile11.setDescription("Kombi vozilo");
    vechile11.setFirstRegistrationDate(LocalDate.of(2009, 4, 3));
    vechile11.setLicencePlateNumber("KT563DN");
    vechile11.setMake("Opel");
    vechile11.setModel("Vivaro");
    vechile11.setModelYear(2008);
    vechile11.setOrganization(organization1);
    vechile11.setVechileNumber("54538045784");
    vechile11.setRegistrationValidUntil(LocalDate.of(2022, 8, 12));
    vechileRepository.save(vechile11);

    final Vechile vechile12 = new Vechile();
    vechile12.setCondition(VechileCondition.USABLE);
    vechile12.setDescription("Navalno vozilo");
    vechile12.setFirstRegistrationDate(LocalDate.of(2001, 3, 8));
    vechile12.setLicencePlateNumber("KT632IO");
    vechile12.setMake("Iveco");
    vechile12.setModel("Magirus");
    vechile12.setModelYear(1999);
    vechile12.setOrganization(organization1);
    vechile12.setVechileNumber("6429827542872");
    vechile12.setRegistrationValidUntil(LocalDate.of(2021, 11, 3));
    vechileRepository.save(vechile12);

    final Vechile vechile21 = new Vechile();
    vechile21.setCondition(VechileCondition.USABLE);
    vechile21.setDescription("Navalno vozilo");
    vechile21.setFirstRegistrationDate(LocalDate.of(2003, 1, 11));
    vechile21.setLicencePlateNumber("KT162HJ");
    vechile21.setMake("MAN");
    vechile21.setModel("ZF-76S");
    vechile21.setModelYear(2001);
    vechile21.setOrganization(organization2);
    vechile21.setVechileNumber("74585866785678567");
    vechile21.setRegistrationValidUntil(LocalDate.of(2022, 2, 7));
    vechileRepository.save(vechile21);

    log.info("Vechile inserted!");

    log.info("Inserting Person...");

    final boolean appUser = true;
    final Person person1 = new Person();
    person1.setBirthDate(LocalDate.of(1997, 7, 4));
    person1.setEmail("iloncari2@tvz.hr");
    person1.setIdentificationNumber("1234567");
    person1.setLastname("Lončarić");
    person1.setName("Igor");
    person1.setProfession(Professions.FIREFIGHTER);
    if (appUser) {
      person1.setHashedPassword(BCrypt.hashpw("sk8EbXy6", BCrypt.gensalt()));
      person1.setUsername("iloncari2");
    }
    personRepository.save(person1);

    final Person person2 = new Person();
    person2.setBirthDate(LocalDate.of(2002, 3, 12));
    person2.setEmail("ivo.ivic@tvz.hr");
    person2.setIdentificationNumber("6546345");
    person2.setLastname("Ivic");
    person2.setName("Ivo");
    person2.setProfession(Professions.FIREFIGHTER);
    personRepository.save(person2);

    log.info("Person inserted!");

    log.info("Inserting PersonOrganizations...");

    final Set<PersonOrganization> pOSet1 = new HashSet<>();

    final PersonOrganization pO1 = new PersonOrganization();
    pO1.setDuty(Duty.SECRETARY);
    pO1.setRole(UserRole.MANAGER);
    pO1.setJoinDate(LocalDate.of(2005, 7, 12));
    pO1.setOrganization(organization1);
    pO1.setExitDate(null);
    pO1.setAppRights(true);
    pO1.setPerson(person1);
    personOrganizationRepository.save(pO1);
    pOSet1.add(pO1);

    final PersonOrganization pO2 = new PersonOrganization();
    pO2.setDuty(Duty.NONE);
    pO2.setJoinDate(LocalDate.of(2002, 1, 16));
    pO2.setOrganization(organization3);
    pO2.setExitDate(LocalDate.of(2004, 9, 15));
    pO2.setAppRights(false);
    pO2.setPerson(person1);
    personOrganizationRepository.save(pO2);
    pOSet1.add(pO2);

    final PersonOrganization pO3 = new PersonOrganization();
    pO3.setDuty(Duty.NONE);
    pO3.setRole(UserRole.MANAGER);
    pO3.setJoinDate(LocalDate.of(2011, 9, 10));
    pO3.setOrganization(organization4);
    pO3.setExitDate(null);
    pO3.setAppRights(true);
    pO3.setPerson(person1);
    personOrganizationRepository.save(pO3);
    pOSet1.add(pO3);

    person1.setOrgList(pOSet1);
    personRepository.save(person1);

    final Set<PersonOrganization> pOSet2 = new HashSet<>();

    final PersonOrganization pO21 = new PersonOrganization();
    pO21.setDuty(Duty.SECRETARY);
    pO21.setRole(UserRole.MANAGER);
    pO21.setJoinDate(LocalDate.of(2005, 7, 12));
    pO21.setOrganization(organization2);
    pO21.setExitDate(null);
    pO21.setAppRights(true);
    pO21.setPerson(person2);
    personOrganizationRepository.save(pO21);
    pOSet2.add(pO21);
    person2.setOrgList(pOSet2);
    personRepository.save(person2);

    log.info("PersonOrganizations inserted");

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
            pO2.setPerson(p1);
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
        pO1.setPerson(p1);
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
