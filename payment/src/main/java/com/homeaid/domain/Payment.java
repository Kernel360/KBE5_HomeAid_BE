package com.homeaid.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount; // 결제 금액

  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private LocalDateTime paidAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reservation_id")
  private Reservation reservation;

}
