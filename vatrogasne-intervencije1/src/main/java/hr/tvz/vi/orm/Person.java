/*
 * User User.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.orm;

import hr.tvz.vi.util.Constants.Gender;
import hr.tvz.vi.util.Constants.Professions;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table
public class Person {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String lastname;

  private LocalDate birthDate;

  private String identificationNumber;

  private String email;

  private String hashedPassword;

  private String username;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  private String phoneNumber;

  private String residenceCity;

  private String residenceStreet;

  private String residenceStreetNumber;

  @Enumerated(EnumType.STRING)
  private Professions profession;

  @ToString.Exclude
  @OneToMany(mappedBy = "person", fetch = FetchType.EAGER)
  private Set<PersonOrganization> orgList;

}
