/*
 * Report Report.java.
 *
 * Copyright (c) 2018 OptimIT d.o.o.. All rights reserved.
 */
package hr.tvz.vi.orm;

import hr.tvz.vi.util.Constants.ReportStatus;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table
public class Report {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  @Enumerated(EnumType.STRING)
  private ReportStatus status;

  private LocalDate creationDate;

  private LocalDate updateDate;

  private String creatorId;

  @ToString.Exclude
  @OneToMany(mappedBy = "report", fetch = FetchType.LAZY)
  private List<hr.tvz.vi.orm.Task> tasks;

  @ManyToOne
  private Organization organizationOwner;
}
