/*
 * FuelConsuption FuelConsuption.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.orm;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table
public class FuelConsuption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate fillingDate;

  private int fuelAmount;

  private Long price;

  @ManyToOne
  @ToString.Exclude
  @JoinColumn(name = "vechileId")
  private Vechile fuelVechile;
}
