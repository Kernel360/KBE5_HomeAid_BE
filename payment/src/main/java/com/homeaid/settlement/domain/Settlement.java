package com.homeaid.settlement.domain;

import com.homeaid.exception.CustomException;
import com.homeaid.settlement.domain.enumerate.SettlementStatus;
import com.homeaid.settlement.exception.SettlementErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

  @Column
  private LocalDateTime confirmedAt; // 승인일

  @Column
  private LocalDateTime paidAt; // 지급일

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SettlementStatus status;

  public void setConfirmedAt(LocalDateTime confirmedAt) {
    this.confirmedAt = confirmedAt;
  }

  public void setPaidAt(LocalDateTime paidAt) {
    this.paidAt = paidAt;
  }

  public void setStatus(SettlementStatus status) {
    this.status = status;
  }

  public void approve() {
    if (this.status != SettlementStatus.PENDING) {
      throw new CustomException(SettlementErrorCode.ALREADY_CONFIRMED);
    }
    this.confirmedAt = LocalDateTime.now();
    this.status = SettlementStatus.APPROVED;
  }

  public void pay() {
    if (this.status != SettlementStatus.APPROVED) {
      throw new CustomException(SettlementErrorCode.NOT_CONFIRMED);
    }
    this.paidAt = LocalDateTime.now();
    this.status = SettlementStatus.PAID;
  }

}
