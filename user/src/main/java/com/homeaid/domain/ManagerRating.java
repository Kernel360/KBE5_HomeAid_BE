package com.homeaid.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Getter
@Table(name = "manager_rating")
@NoArgsConstructor
public class ManagerRating {

  @Id
  @Column(name = "manager_id")
  private Long managerId;

  @Column(name = "review_count", nullable = false)
  private Integer reviewCount;

  @Column(name = "average_rating", nullable = false)
  private Double averageRating;

  @Column(name = "last_updated", nullable = false)
  @LastModifiedDate
  private LocalDateTime lastUpdated;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "manager_id")
  @MapsId // manager_id 를 PK로 사용
  private Manager manager;

  public ManagerRating(Manager manager){
    this.managerId = manager.getId();
    this.reviewCount = 0 ;
    this.averageRating = 0.0;
    this.manager = manager;
  }

  public void updateRating(Integer reviewCount, double averageRating) {
    this.averageRating = averageRating;
    this.reviewCount = reviewCount;
  }

}
