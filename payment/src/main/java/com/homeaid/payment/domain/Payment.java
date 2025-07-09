package com.homeaid.payment.domain;

import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.exception.CustomException;
import com.homeaid.payment.domain.enumerate.PaymentMethod;
import com.homeaid.payment.domain.enumerate.PaymentStatus;
import com.homeaid.payment.exception.PaymentErrorCode;
import com.homeaid.payment.exception.RefundErrorCode;
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

  private Integer amount; // 결제 금액

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

  @Builder.Default
  @Column(nullable = false)
  private Integer refundedAmount = 0;  // 부분환불 누적 금액

  // 결제에 대해 전체 환불 처리
  public void markRefunded() {
    this.status = PaymentStatus.REFUNDED;
    this.refundedAmount = this.amount;
  }

  // 결제에 부분 환불 적용
  public void applyPartialRefund(int refundAmount) {
    int total = this.refundedAmount + refundAmount;
    if (total > this.amount) {
      throw new CustomException(PaymentErrorCode.REFUND_AMOUNT_EXCEEDS_PAYMENT);
    }
    this.refundedAmount = total;
    this.status = (total == this.amount) ? PaymentStatus.REFUNDED : PaymentStatus.PARTIAL_REFUNDED;
  }

  // 결제 취소 처리
  public void cancelPayment() {
    this.status = PaymentStatus.CANCELED;
    this.refundedAmount = this.amount;
  }

  // 결제 취소 비즈니스 로직 - 예약 상태 및 결제 상태 검증 후 취소 처리
  public void cancel(ReservationStatus reservationStatus) {
    validateCancellableStatus(reservationStatus);
    validateNotAlreadyRefundedOrCanceled();
    this.status = PaymentStatus.CANCELED;
    this.refundedAmount = this.amount;
  }

  // 취소 가능 상태인지 검증
  private void validateCancellableStatus(ReservationStatus reservationStatus) {
    if (!isCancellableStatus(reservationStatus)) {
      throw new CustomException(PaymentErrorCode.PAYMENT_CANCELLATION_NOT_ALLOWED);
    }
  }

  // 예약 상태가 결제 취소 가능 상태인지 검증
  private boolean isCancellableStatus(ReservationStatus status) {
    return status == ReservationStatus.REQUESTED
        || status == ReservationStatus.MATCHING
        || status == ReservationStatus.MATCHED;
  }

  // 이미 환불되었거나 취소된 결제는 중복 취소/횐불 불가 검증
  private void validateNotAlreadyRefundedOrCanceled() {
    if (this.status == PaymentStatus.REFUNDED || this.status == PaymentStatus.CANCELED) {
      throw new CustomException(PaymentErrorCode.PAYMENT_ALREADY_REFUNDED);
    }
  }

  // 전체 환불 처리 - 예약 상태를 기반으로 환불 가능성 검증 후 전액 환불 처리
  public void refund(ReservationStatus reservationStatus) {
    validateRefundableStatus(reservationStatus);
    validateNotAlreadyRefunded();
    this.status = PaymentStatus.REFUNDED;
    this.refundedAmount = this.amount;
  }

  // 예약 상태로 환불 가능성 검증 - 환불 가능 상태가 아니면 예외 발생
  private void validateRefundableStatus(ReservationStatus reservationStatus) {
    if (!isRefundableStatus(reservationStatus)) {
      throw new CustomException(RefundErrorCode.PAYMENT_REFUND_NOT_ALLOWED);
    }
  }

  // 환불 가능 상태인지 확인
  private boolean isRefundableStatus(ReservationStatus status) {
    return status == ReservationStatus.REQUESTED
        || status == ReservationStatus.MATCHING
        || status == ReservationStatus.MATCHED;
  }

  // 이미 전액 환불된 결제인지 검증
  private void validateNotAlreadyRefunded() {
    if (this.status == PaymentStatus.REFUNDED) {
      throw new CustomException(PaymentErrorCode.PAYMENT_ALREADY_REFUNDED);
    }
  }

  // 부분 환불 처리 - 예약 상태 검증 후 환불 진행
  public void partialRefund(ReservationStatus reservationStatus, int refundAmount) {
    validatePartialRefundableStatus(reservationStatus);
    validateNotAlreadyRefunded();
    applyPartialRefund(refundAmount);
  }

  // 부분 환불 가능 상태 검증 - COMPLETED 상태일때만 가능
  private void validatePartialRefundableStatus(ReservationStatus reservationStatus) {
    if (reservationStatus != ReservationStatus.COMPLETED) {
      throw new CustomException(RefundErrorCode.PAYMENT_REFUND_NOT_ALLOWED);
    }
  }

}
