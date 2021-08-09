/*
 * Vechile Vechile.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.orm;

import hr.tvz.vi.util.Constants.VechileCondition;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table
public class Vechile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String make;

  private String model;

  private int modelYear;

  private String licencePlateNumber;

  private LocalDate registrationValidUntil;

  private String vechileNumber;

  private LocalDate firstRegistrationDate;

  private String description;

  @Enumerated(EnumType.STRING)
  private VechileCondition condition;

  @ManyToOne
  @ToString.Exclude
  @JoinColumn(name = "organizationId")
  private Organization organization;

  @ToString.Exclude
  @OneToMany(mappedBy = "serviceVechile", fetch = FetchType.LAZY)
  private List<Service> services;

  @ToString.Exclude
  @OneToMany(mappedBy = "fuelVechile", fetch = FetchType.LAZY)
  private List<FuelConsuption> fuelConsuptions;
}
