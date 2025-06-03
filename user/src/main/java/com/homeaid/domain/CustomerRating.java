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
@Table(name = "customer_rating")
@NoArgsConstructor
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

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  @MapsId // customer_id 를 PK로 사용
  private Customer customer;

  public CustomerRating(Customer customer){
    this.customerId = customer.getId();
    this.reviewCount = 0;
    this.averageRating = 0.0;
    this.customer = customer;
  }

  public void updateRating(Integer reviewCount, Double averageRating){
    this.averageRating = averageRating;
    this.reviewCount = reviewCount;
  }

}
