package com.homeaid.domain;

import com.homeaid.worklog.domain.enumerate.Weekday;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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

  private LocalTime startTime;

  private LocalTime endTime;

  @OneToMany(mappedBy = "availability", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ManagerPreferRegion> preferRegions = new ArrayList<>();

  @Builder
  public ManagerAvailability(Manager manager, Weekday weekday,
      LocalTime startTime, LocalTime endTime) {
    this.manager = manager;
    this.weekday = weekday;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public void addPreferRegion(ManagerPreferRegion region) {
    region.setAvailability(this);
    this.preferRegions.add(region);
  }

}
