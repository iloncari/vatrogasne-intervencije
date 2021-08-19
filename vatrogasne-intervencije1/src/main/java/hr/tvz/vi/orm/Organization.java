/*
 * Organization Organization.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.orm;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table
@EqualsAndHashCode(exclude = "childs")
public class Organization {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String identificationNumber;

  private LocalDate establishmentDate;

  private String iban;

  private String city;

  private String street;

  private String streetNumber;

  private String email;

  private String url;

  private String contactNumber;
  /*
   * @ToString.Exclude
   *
   * @OneToMany(mappedBy = "organization", fetch = FetchType.EAGER)
   * private List<Vechile> vechiles;
   */
  /*
   * @ToString.Exclude
   *
   * @OneToMany(mappedBy = "organizationOwner", fetch = FetchType.LAZY)
   * private List<Report> reports;
   */

  @Column(name = "parent_id", insertable = false, updatable = false)
  private Integer parentId;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "parent_id")
  private Set<Organization> childs;

}
