package com.homeaid.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "manager_rating")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

  public ManagerRating(Long managerId) {
    this.managerId = managerId;
    this.reviewCount = 0;
    this.averageRating = 0.0;
  }

  public void updateRating(Integer reviewCount, double averageRating) {
    this.averageRating = averageRating;
    this.reviewCount = reviewCount;
  }

}
