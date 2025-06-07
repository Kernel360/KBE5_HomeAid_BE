package com.homeaid.domain;

import com.homeaid.domain.enumerate.Weekday;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Weekday weekday;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "manager_id")
  private Manager manager;

  private LocalDate date;

  private LocalTime startTime;

  private LocalTime endTime;

  private Double latitude;

  private Double longitude;

  @Builder
  public ManagerAvailability(Manager manager, Weekday weekday,
      Double latitude, Double longitude,
      LocalTime startTime, LocalTime endTime) {
    this.manager = manager;
    this.weekday = weekday;
    this.latitude = latitude;
    this.longitude = longitude;
    this.startTime = startTime;
    this.endTime = endTime;
  }

}
