/*
 * PersonOrganization PersonOrganization.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.orm;

import hr.tvz.vi.util.Constants.Duty;
import hr.tvz.vi.util.Constants.UserRole;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table
@EqualsAndHashCode(exclude = "person")
public class PersonOrganization {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn
  @OneToOne
  private Organization organization;

  private LocalDate requestDate;

  private LocalDate joinDate;

  private LocalDate exitDate;

  private boolean appRights;

  @Enumerated(EnumType.STRING)
  private Duty duty;

  @Enumerated(EnumType.STRING)
  private UserRole role;

  @ManyToOne
  @ToString.Exclude
  @JoinColumn(name = "personId")
  private Person person;
}
