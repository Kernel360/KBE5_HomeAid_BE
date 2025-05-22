package com.example.homeaid.manager.manager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "manager_availability")
public class ManagerAvailability {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "manager_id")
  private Manager manager;

  private LocalDate availableDate;

  private LocalTime startTime;
  private LocalTime endTime;

  private String region;

  @Builder
  public ManagerAvailability(LocalDate availableDate, LocalTime startTime, LocalTime endTime,
      String region) {
    this.availableDate = availableDate;
    this.startTime = startTime;
    this.endTime = endTime;
    this.region = region;
  }

}
