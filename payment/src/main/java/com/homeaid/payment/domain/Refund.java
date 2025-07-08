package com.homeaid.payment.domain;

import com.homeaid.exception.CustomException;
import com.homeaid.payment.domain.enumerate.RefundReason;
import com.homeaid.payment.domain.enumerate.RefundStatus;
import com.homeaid.payment.exception.RefundErrorCode;
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
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "refund")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 직접 new 못하게 방지
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

  // 팩토리 메서드: 전액 환불
  public static Refund manualRefund(Payment payment, int amount, RefundReason reason, String adminComment) {
    return new Refund(
        payment,
        reason,
        RefundStatus.COMPLETED,
        amount,
        null,
        adminComment,
        null,
        LocalDateTime.now()
    );
  }

  // 팩토리 메서드: 고객 요청
  public static Refund customerRequest(Payment payment, int amount, RefundReason reason, String customerComment) {
    return new Refund(
        payment,
        reason,
        RefundStatus.REQUESTED,
        amount,
        customerComment,
        null,
        LocalDateTime.now(),
        null
    );
  }

  public Refund approve(String adminComment) {
    if (this.status != RefundStatus.REQUESTED) {
      throw new CustomException(RefundErrorCode.CANNOT_APPROVE_REFUND);
    }
    this.status = RefundStatus.APPROVED;
    this.adminComment = adminComment;
    this.processedAt = LocalDateTime.now();
    return this;
  }

  public Refund reject(String adminComment) {
    if (this.status != RefundStatus.REQUESTED) {
      throw new CustomException(RefundErrorCode.CANNOT_REJECT_REFUND);
    }
    this.status = RefundStatus.REJECTED;
    this.adminComment = adminComment;
    this.processedAt = LocalDateTime.now();
    return this;
  }

  public Refund cancel() {
    if (this.status != RefundStatus.REQUESTED) {
      throw new CustomException(RefundErrorCode.CANNOT_CANCEL_REFUND);
    }
    this.status = RefundStatus.CANCELLED;
    this.processedAt = LocalDateTime.now();
    return this;
  }

  // 내부 생성자 (정적 팩토리만 통해 생성되도록)
  private Refund(Payment payment, RefundReason reason, RefundStatus status, Integer refundAmount,
      String customerComment, String adminComment, LocalDateTime requestedAt, LocalDateTime processedAt) {
    this.payment = payment;
    this.reason = reason;
    this.status = status;
    this.refundAmount = refundAmount;
    this.customerComment = customerComment;
    this.adminComment = adminComment;
    this.requestedAt = requestedAt;
    this.processedAt = processedAt;
  }

}