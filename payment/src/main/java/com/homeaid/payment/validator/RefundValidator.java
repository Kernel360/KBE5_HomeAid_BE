package com.homeaid.payment.validator;

import com.homeaid.exception.CustomException;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.Refund;
import com.homeaid.payment.domain.enumerate.RefundStatus;
import com.homeaid.payment.exception.PaymentErrorCode;
import com.homeaid.payment.exception.RefundErrorCode;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.payment.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefundValidator {

  private final RefundRepository refundRepository;
  private final PaymentRepository paymentRepository;

  // 결제 소유자가 맞는지 검증
  public void validateOwnership(Payment payment, Long userId) {
    if (!payment.getReservation().getCustomerId().equals(userId)) {
      throw new CustomException(PaymentErrorCode.PAYMENT_ACCESS_DENIED);
    }
  }

  // 결제에 대한 중복 환불 요청 여부 검증
  public void validateDuplicateRefund(Payment payment) {
    boolean exists = refundRepository.existsByPaymentIdAndStatus(payment.getId(), RefundStatus.REQUESTED);
    if (exists) {
      throw new CustomException(RefundErrorCode.DUPLICATE_REFUND_REQUEST);
    }
  }

  // 결제 ID 기준 결제 조회 & 없으면 예외 발생
  public Payment getPaymentOrThrow(Long paymentId) {
    return paymentRepository.findById(paymentId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));
  }

  // 회원 소유의 환불 내역 조회 & 없으면 예외 발생
  public Refund getRefundOrThrow(Long refundId, Long userId) {
    return refundRepository.findByIdAndPayment_Reservation_CustomerId(refundId, userId)
        .orElseThrow(() -> new CustomException(RefundErrorCode.REFUND_NOT_FOUND));
  }

  // 관리자
  // 요청 상태인지 검증 (승인/거절 공통)
  public void validateRefundStatusIsRequest(Refund refund) {
    if (refund.getStatus() != RefundStatus.REQUESTED) {
      throw new CustomException(RefundErrorCode.INVALID_REFUND_STATUS);
    }
  }

  // getRefundOrThrow: 회원 소유 검증 시 사용
  public Refund getRefundOrThrow(Long refundId) {
    return refundRepository.findById(refundId)
        .orElseThrow(() -> new CustomException(RefundErrorCode.REFUND_NOT_FOUND));
  }

}
