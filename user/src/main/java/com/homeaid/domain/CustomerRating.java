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
@Table(name = "customer_rating")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CustomerRating {

  @Id
  @Column(name = "customer_id")
  private Long customerId;

  @Column(name = "review_count", nullable = false)
  private Integer reviewCount;

  @Column(name = "average_rating", nullable = false)
  private Double averageRating;

  @Column(name = "last_updated", nullable = false)
  @LastModifiedDate
  private LocalDateTime lastUpdated;

  public CustomerRating(Long customerId) {
    this.customerId = customerId;
    this.reviewCount = 0;
    this.averageRating = 0.0;
  }

  public void updateRating(int reviewCount, double averageRating) {
    this.averageRating = averageRating;
    this.reviewCount = reviewCount;
  }

}
