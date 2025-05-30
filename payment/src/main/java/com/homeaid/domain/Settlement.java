package com.homeaid.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "settlement")
@EntityListeners(AuditingEntityListener.class)
public class Settlement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer totalAmount;

  @Column(nullable = false)
  private Integer managerSettlementPrice;

  @Column(nullable = false)
  private LocalDate settlementWeekStart;

  @Column(nullable = false)
  private LocalDate settlementWeekEnd;

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private LocalDateTime settledAt;

  @Column(nullable = false)
  private Long managerId;

}
