package com.homeaid.payment.domain;

import com.homeaid.payment.domain.enumerate.RefundReason;
import com.homeaid.payment.domain.enumerate.RefundStatus;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refund")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class Refund {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 어떤 결제에 대한 환불인지 연결
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id", nullable = false)
  private Payment payment;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RefundReason reason; // 환불 사유(정책)

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RefundStatus status; // 환불 상태

  @Column(nullable = false)
  private Integer refundAmount; // 환불 금액

  private String customerComment; // 고객이 남긴 요청 메시지

  private String adminComment; // 관리자가 승인/거절한 메시지

  private LocalDateTime requestedAt; // 고객 환불 요청 시각

  private LocalDateTime processedAt; // 관리자 승인/거절 시각

}