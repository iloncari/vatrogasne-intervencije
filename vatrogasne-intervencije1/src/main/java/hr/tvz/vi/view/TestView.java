/*
 * TestView TestView.java.
 *
 */
package hr.tvz.vi.view;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import hr.tvz.vi.orm.FuelConsuptionRepository;
import hr.tvz.vi.orm.Organization;
import hr.tvz.vi.orm.OrganizationRepository;
import hr.tvz.vi.orm.Person;
import hr.tvz.vi.orm.PersonOrganization;
import hr.tvz.vi.orm.PersonOrganizationRepository;
import hr.tvz.vi.orm.PersonRepository;
import hr.tvz.vi.orm.ReportRepository;
import hr.tvz.vi.orm.ServiceRepository;
import hr.tvz.vi.orm.TaskRepository;
import hr.tvz.vi.orm.VechileRepository;
import hr.tvz.vi.util.Constants.Duty;
import hr.tvz.vi.util.Constants.Gender;
import hr.tvz.vi.util.Constants.Professions;
import hr.tvz.vi.util.Constants.UserRole;
import hr.tvz.vi.util.OrganziationConstants;
import hr.tvz.vi.util.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.util.CollectionUtils;
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

  final List<String> oibList = new ArrayList<>();

  final Map<String, Duty> personDutyPerPersonOib = new HashMap<>();
  @Autowired
  PersonOrganizationRepository personOrganizationRepository;
  @Autowired
  OrganizationRepository organizationRepo;
  @Autowired
  TaskRepository taskRepository;
  @Autowired
  ReportRepository reportRepository;

  Map<String, List<Person>> personPerOrgName = new HashMap<>();

  /**
   * Instantiates a new test view.
   */
  public TestView() {
    final TextField operationTextFiedl = new TextField("operacija");
    final VButton reinitButton = new VButton("izvrsi operaciju", e -> {
      if ("show".equals(operationTextFiedl.getValue())) {
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

  /**
   * Clear.
   */
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

  /**
   * Extract person from organization.
   *
   * @param redovi the redovi
   * @return the list
   */
  private List<Person> extractPersonFromOrganization(List<String> redovi) {
    final List<Person> members = new ArrayList<>();
    Arrays.asList("Predsjednik:", "Zapovjednik:", "Tajnik:").forEach(titula -> {
      String personRed = redovi.stream().filter(red -> red.startsWith(titula)).findFirst().orElse(null);
      if (StringUtils.isNotBlank(personRed)) {
        personRed = personRed.replace(titula, "").trim();
        final List<String> personPodaciList = Arrays.asList(personRed.split(","));
        if (!personPodaciList.isEmpty()) {
          final String personNameLastName = personPodaciList.get(0).trim();
          final String name = personNameLastName.split(" ")[0];
          final String lastname = personNameLastName.split(" ")[1];
          String mob = personPodaciList.stream().filter(perPodaci -> perPodaci.trim().startsWith("mob:")).findFirst().orElse(null);
          if (StringUtils.isNotBlank(mob)) {
            mob = mob.replace("mob:", "").trim();
          }
          String personMail = personPodaciList.stream().filter(perPodaci -> perPodaci.trim().startsWith("email:")).findFirst().orElse(null);
          if (StringUtils.isNotBlank(personMail)) {
            personMail = personMail.replace("email:", "").trim();
          }
          final Person person = getExistingPersonOrNew(name, lastname);
          if (person.getIdentificationNumber() == null) {
            person.setBirthDate(genLocalDate(1950, 2000));
            person.setEmail(personMail);
            person.setGender(Gender.MALE);
            person.setIdentificationNumber(generateOIB());
            person.setLastname(lastname);
            person.setHashedPassword(BCrypt.hashpw(name.concat(lastname), BCrypt.gensalt()));
            person.setName(name);
            person.setPhoneNumber(mob);
            person.setProfession(Arrays.asList(Professions.values()).get(RandomUtils.nextInt(0, Arrays.asList(Professions.values()).size() - 1)));
            person.setUsername(name.concat(lastname));
            final Duty duty = titula.equals("Predsjednik:") ? Duty.PRESIDENT : (titula.equals("Tajnik:") ? Duty.SECRETARY : Duty.COMMANDER);
            personDutyPerPersonOib.put(person.getIdentificationNumber(), duty);
          }
          if (!members.stream().anyMatch(m -> m.getName().equals(person.getName()))) {
            members.add(person);
          }
        }

      }
    });

    return members;
  }

  /**
   * Generate OIB.
   *
   * @return the string
   */
  private String generateOIB() {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    String oib = String.valueOf(random.nextLong(10_000_000_000L, 100_000_000_000L));
    while (oibList.contains(oib)) {
      oib = String.valueOf(random.nextLong(10_000_000_000L, 100_000_000_000L));
    }
    return oib;
  }

  /**
   * Gen local date.
   *
   * @param miny the miny
   * @param maxy the maxy
   * @return the local date
   */
  private LocalDate genLocalDate(int miny, int maxy) {
    final int month = RandomUtils.nextInt(1, 12);
    int day = RandomUtils.nextInt(1, 30);
    if (month == 2 && day > 28) {
      day = 20;
    }
    return LocalDate.of(RandomUtils.nextInt(miny, maxy), month, day);
  }

  private Person getExistingPersonOrNew(String name, String lastname) {
    final List<Person> lista = personPerOrgName.values().stream().filter(listaOsoba -> {
      return listaOsoba.stream().anyMatch(osoba -> osoba.getName().equals(name) && osoba.getLastname().equals(lastname));
    }).findAny().orElse(new ArrayList<Person>());
    return lista.stream().filter(osoba -> osoba.getName().equals(name) && osoba.getLastname().equals(lastname)).findAny().orElse(new Person());
  }

  /**
   * Gets the red.
   *
   * @param start the start
   * @param redovi the redovi
   * @return the red
   */
  private String getRed(String start, List<String> redovi) {
    String red = redovi.stream().filter(perPodaci -> perPodaci.startsWith(start)).findFirst().orElse(null);
    if (StringUtils.isNotBlank(red)) {
      red = red.replace(start, "").trim();
    }

    return red;
  }

  /**
   * Inits the real data.
   */
  private void initRealData() {
    clear();
    Utils.showSuccessNotification(2000, Position.TOP_CENTER, "Baza inicijalizirana");

    final Map<String, Organization> zajednicaZupanijeOrg = new HashMap<>();
    final Map<String, Organization> zajednicaOPcineGrada = new HashMap<>();
    final Map<String, Organization> zajednicaDVDMap = new HashMap<>();
    final Map<String, List<String>> nazivChildsVZO = new HashMap<>();
    final Map<String, List<String>> nazivChildsDVD = new HashMap<>();
    personPerOrgName.clear();

    personDutyPerPersonOib.clear();
    oibList.clear();
    // zajednica zupanija
    Arrays.asList(OrganziationConstants.vatroganeZajedniceZupanija.split("Next\\r\\n")).stream().forEach(zajednicaZupanije -> {
      final List<String> redovi = Arrays.asList(zajednicaZupanije.split("\\r\\n")).stream().collect(Collectors.toList());
      String streetNumber = null;
      String street = null;
      String city = null;

      final String naziv = redovi.get(0);
      String phone = getRed("tel:", redovi);
      if (phone != null && phone.contains(",")) {
        phone = Arrays.asList(phone.split(",")).get(0);
      }
      final String email = getRed("email:", redovi);
      final String url = getRed("url:", redovi);

      // Vatrogasna 1, 44000 Sisak
      final String adresa = redovi.stream()
        .filter(red -> red.contains(",") && (red.contains("1") || red.contains("0") || red.contains("2") || red.contains("3") || red.contains("4")
          || red.contains("5") || red.contains("6") || red.contains("7") || red.contains("8") || red.contains("9")))
        .findFirst().orElse(null);
      if (StringUtils.isNotBlank(adresa)) {
        final List<String> adressComma = Arrays.asList(adresa.split(","));
        if (!adressComma.isEmpty()) {
          final String streetAndStreetNumber = new String(adressComma.get(0)).replace("č", "c").replace("ć", "c").replace("š", "s").replace("ž", "z")
            .replace("đ", "d").replace("dž", "dz").trim();
          streetNumber = streetAndStreetNumber.replaceAll("\\b[^\\d\\W]+\\b", "").trim();
          street = streetAndStreetNumber.replaceAll("\\d+", "").trim();
          city = adressComma.get(1).trim().replaceAll("\\d+", "").trim();
        }
      }
      personPerOrgName.put(naziv, extractPersonFromOrganization(redovi));
      final Organization saveOrgZ = saveOrganization(naziv, city, phone, email, street, streetNumber, url);
      zajednicaZupanijeOrg.put(naziv, saveOrgZ);
    });

    log.info("\n");
    log.info("___________________VZO________________");
    log.info("\n");
    Arrays.asList(OrganziationConstants.vatroganeZajedniceGradaOpcine.split("Next\\r\\n")).stream().forEach(zajednicaOpcine -> {
      final List<String> redovi = Arrays.asList(zajednicaOpcine.split("\\r\\n")).stream().collect(Collectors.toList());
      String streetNumber = null;
      String street = null;
      String city = null;

      final String naziv = redovi.get(1);
      String phone = getRed("tel:", redovi);
      if (phone != null && phone.contains(",")) {
        phone = Arrays.asList(phone.split(",")).get(0);
      }
      final String email = getRed("email:", redovi);
      final String url = getRed("url:", redovi);
      final String adresa = redovi.stream()
        .filter(red -> red.contains(",") && (red.contains("1") || red.contains("0") || red.contains("2") || red.contains("3") || red.contains("4")
          || red.contains("5") || red.contains("6") || red.contains("7") || red.contains("8") || red.contains("9")))
        .findFirst().orElse(null);
      if (StringUtils.isNotBlank(adresa)) {
        final List<String> adressComma = Arrays.asList(adresa.split(","));
        if (!adressComma.isEmpty()) {
          final String streetAndStreetNumber = new String(adressComma.get(0)).replace("č", "c").replace("ć", "c").replace("š", "s").replace("ž", "z")
            .replace("đ", "d").replace("dž", "dz").trim();
          streetNumber = streetAndStreetNumber.replaceAll("\\b[^\\d\\W]+\\b", "").trim();
          street = streetAndStreetNumber.replaceAll("\\d+", "").trim();
          city = adressComma.get(1).trim().replaceAll("\\d+", "").trim();
        }
      }
      personPerOrgName.put(naziv, extractPersonFromOrganization(redovi));
      final Organization saveOrgOpc = saveOrganization(naziv, city, phone, email, street, streetNumber, url);

      zajednicaOPcineGrada.put(naziv, saveOrgOpc);
      final String parentNaziv = redovi.stream().filter(red -> red.contains("županije")).findFirst().orElse(null);

      if (StringUtils.isNotBlank(parentNaziv)) {
        if (nazivChildsVZO.containsKey(parentNaziv)) {
          nazivChildsVZO.get(parentNaziv).add(saveOrgOpc.getName());
        } else {
          final List<String> l1 = new ArrayList<>();
          l1.add(saveOrgOpc.getName());
          nazivChildsVZO.put(parentNaziv, l1);
        }
      }
    });

    final Map<String, List<String>> opcinaGradovi = new HashMap<>();
    Arrays.asList(OrganziationConstants.gradOpcina.split("Next\\r\\n")).forEach(gO -> {
      final String gradOpcina = gO.replace("\\r\\n", "").trim();
      final List<String> gradOpcinaList = Arrays.asList(gradOpcina.split(","));
      if (opcinaGradovi.containsKey(gradOpcinaList.get(1).toLowerCase())) {
        opcinaGradovi.get(gradOpcinaList.get(1).toLowerCase()).add(gradOpcinaList.get(0).trim().toLowerCase());
      } else {
        final List<String> gradovu = new ArrayList<>();
        gradovu.add(gradOpcinaList.get(0).trim().toLowerCase());
        opcinaGradovi.put(gradOpcinaList.get(1).trim().toLowerCase(), gradovu);
      }
    });

    log.info("\n");
    log.info("___________________DVD________________");
    log.info("\n");

    Arrays.asList(OrganziationConstants.orgDVD.split("Next\\r\\n")).stream().forEach(zajednicaDVD -> {
      final List<String> redovi = Arrays.asList(zajednicaDVD.split("\\r\\n")).stream().collect(Collectors.toList());
      String streetNumber = null;
      String street = null;
      String city = null;

      final String naziv = redovi.stream().filter(red -> red.startsWith("Dobrovoljno vatrogasno")).findFirst().orElse("");
      String phone = getRed("tel:", redovi);
      if (phone != null && phone.contains(",")) {
        phone = Arrays.asList(phone.split(",")).get(0);
      }
      final String email = getRed("email:", redovi);
      final String url = getRed("url:", redovi);
      final String adresa = redovi.stream()
        .filter(red -> red.contains(",") && (red.contains("1") || red.contains("0") || red.contains("2") || red.contains("3") || red.contains("4")
          || red.contains("5") || red.contains("6") || red.contains("7") || red.contains("8") || red.contains("9")))
        .findFirst().orElse(null);
      if (StringUtils.isNotBlank(adresa)) {
        final List<String> adressComma = Arrays.asList(adresa.split(","));
        if (!adressComma.isEmpty()) {
          final String streetAndStreetNumber = new String(adressComma.get(0)).replace("č", "c").replace("ć", "c").replace("š", "s").replace("ž", "z")
            .replace("đ", "d").replace("dž", "dz").trim();
          streetNumber = streetAndStreetNumber.replaceAll("\\b[^\\d\\W]+\\b", "").trim();
          street = streetAndStreetNumber.replaceAll("\\d+", "").trim();
          city = adressComma.get(1).trim().replaceAll("\\d+", "").trim();
        }
      }
      personPerOrgName.put(naziv, extractPersonFromOrganization(redovi));
      final Organization saveOrgOpc = saveOrganization(naziv, city, phone, email, street, streetNumber, url);

      zajednicaDVDMap.put(naziv, saveOrgOpc);

      final String parent = redovi.get(0);
      final List<String> vzos = nazivChildsVZO.get(parent.trim());

      String opcina = "";
      for (final Entry<String, List<String>> opcinaGrads : opcinaGradovi.entrySet()) {
        final String mjestoNaziv = naziv.replace("Dobrovoljno vatrogasno društvo ", "").toLowerCase();
        if (opcinaGrads.getValue().stream().anyMatch(naselje -> mjestoNaziv.equals(naselje))) {
          opcina = opcinaGrads.getKey();
        }
      }

      if (StringUtils.isNotBlank(opcina) && !CollectionUtils.isEmpty(vzos)) {
        final String opcinaGrad = opcina;
        final Optional<String> matchedVZO = vzos.stream().filter(vzo -> !vzo.startsWith("Javna vatrogasna postrojba")).filter(vzo -> {
          if (vzo.toLowerCase().startsWith("vatrogasna zajednica grada ")) {
            final String vzo1 = vzo.toLowerCase().replace("vatrogasna zajednica grada ", "");
            return opcinaGrad.contains(vzo1.substring(0, vzo1.length() - 2));
          } else if (vzo.toLowerCase().startsWith("vatrogasna zajednica općine ")) {
            return vzo.toLowerCase().contains(opcinaGrad);
          }
          return false;
        }).findFirst();
        if (matchedVZO.isPresent()) {
          if (nazivChildsDVD.get(matchedVZO.get()) != null) {
            nazivChildsDVD.get(matchedVZO.get()).add(naziv);
          } else {
            final List<String> nazivs = new ArrayList<>();
            nazivs.add(naziv);
            nazivChildsDVD.put(matchedVZO.get(), nazivs);
          }

        }
      }
    });

    nazivChildsDVD.forEach((vzo, childs) -> {
      final Organization parent = zajednicaOPcineGrada.get(vzo);
      if (parent == null) {
        return;
      }
      final Set<Organization> childOrgs = new HashSet<>();
      childs.forEach(child -> {
        final Organization org = zajednicaDVDMap.get(child);
        if (org != null) {
          childOrgs.add(org);
        }
      });
      parent.setChilds(childOrgs);
      organizationRepo.save(parent);
    });

    nazivChildsVZO.forEach((vzz, childs) -> {
      final Organization parent = zajednicaZupanijeOrg.get(vzz);
      if (parent == null) {
        return;
      }
      final Set<Organization> childOrgs = new HashSet<>();
      childs.forEach(child -> {
        final Organization org = zajednicaOPcineGrada.get(child);
        if (org != null) {
          childOrgs.add(org);
        }
      });
      parent.setChilds(childOrgs);
      organizationRepo.save(parent);
    });

    final List<Organization> organizacije = organizationRepo.findAll();
    personPerOrgName.forEach((naziv, clanovi) -> {
      final Organization org = organizacije.stream().filter(o -> o.getName().equals(naziv)).findFirst().orElse(null);
      if (org == null) {
        return;
      }
      clanovi.forEach(clan -> {
        final Person p = personRepository.save(clan);
        final PersonOrganization pooo = savePersonOrganization(p, org);
      });
    });

    Utils.showSuccessNotification(2000, Position.TOP_CENTER, "Baza inicijalizirana");
  }

  /**
   * Save organization.
   *
   * @param name the name
   * @param city the city
   * @param phone the phone
   * @param email the email
   * @param street the street
   * @param streetN the street N
   * @param url the url
   * @return the organization
   */
  private Organization saveOrganization(String name, String city, String phone, String email, String street, String streetN,
    String url) {
    final String oib = generateOIB();
    final Organization organization = new Organization();
    organization.setCity(city);
    organization.setContactNumber(phone);
    organization.setEmail(email);
    organization.setIban("HR" + oib);
    organization.setIdentificationNumber(oib);
    organization.setName(name);
    organization.setEstablishmentDate(genLocalDate(1890, 2020));
    organization.setStreet(street);
    organization.setStreetNumber(streetN);
    organization.setUrl(url);
    return organizationRepo.save(organization);
  }

  /**
   * Save person organization.
   *
   * @param person the person
   * @param organization the organization
   * @return the person organization
   */
  private PersonOrganization savePersonOrganization(Person person, Organization organization) {
    final PersonOrganization po = new PersonOrganization();
    po.setAppRights(true);
    po.setDuty(personDutyPerPersonOib.getOrDefault(person.getIdentificationNumber(), Duty.NONE));
    po.setJoinDate(genLocalDate(1956, 2020));
    po.setOrganization(organization);
    po.setPerson(person);
    po.setRole(UserRole.MANAGER);
    return personOrganizationRepository.save(po);
  }

  /**
   * Show.
   */
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

  /**
   * User for login.
   */
  private void userForLogin() {
    personRepository.findAll().stream().filter(person -> {
      if (StringUtils.isAnyBlank(person.getUsername(), person.getHashedPassword())) {
        return false;
      }
      return person.getOrgList().stream().anyMatch(po -> po.getExitDate() == null && po.getOrganization().getParentId() != null);

    }).findAny().ifPresent(p -> Notification.show(p.getUsername() + " " + p.getHashedPassword()));
  }
}
