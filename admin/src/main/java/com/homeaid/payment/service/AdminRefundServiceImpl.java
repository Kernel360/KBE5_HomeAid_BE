package com.homeaid.payment.service;

import com.homeaid.exception.CustomException;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.Refund;
import com.homeaid.payment.domain.enumerate.RefundStatus;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.payment.dto.response.RefundResponseDto;
import com.homeaid.payment.exception.PaymentErrorCode;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.payment.repository.RefundRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminRefundServiceImpl implements AdminRefundService {

  private final PaymentRepository paymentRepository;
  private final AdminPaymentService adminPaymentService;
  private final RefundRepository refundRepository;

  @Override
  @Transactional
  public PaymentResponseDto refundFull(Long paymentId) {
    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

    payment.refund(payment.getReservation().getStatus());

    return adminPaymentService.getPayment(paymentId);
  }

  @Override
  @Transactional
  public PaymentResponseDto refundPartial(Long paymentId, int refundAmount) {
    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

    payment.partialRefund(payment.getReservation().getStatus(), refundAmount);

    return adminPaymentService.getPayment(paymentId);
  }

  @Override
  @Transactional
  public RefundResponseDto approveRefund(Long refundId, String adminComment) {
    Refund refund = refundRepository.findById(refundId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.REFUND_NOT_FOUND));

    // 결제에 부분환불 적용
    refund.getPayment().applyPartialRefund(refund.getRefundAmount());

    // 상태 및 관리자 코멘트 업데이트
    refund = refund.toBuilder()
        .status(RefundStatus.APPROVED)
        .adminComment(adminComment)
        .processedAt(LocalDateTime.now())
        .build();

    Refund updated = refundRepository.save(refund);

    log.info("[RefundApprove] refundId={} status=APPROVED adminComment={}", refundId, adminComment);

    return RefundResponseDto.from(updated);
  }

  @Override
  @Transactional
  public RefundResponseDto rejectRefund(Long refundId, String adminComment) {
    Refund refund = refundRepository.findById(refundId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.REFUND_NOT_FOUND));

    refund = refund.toBuilder()
        .status(RefundStatus.REJECTED)
        .adminComment(adminComment)
        .processedAt(LocalDateTime.now())
        .build();

    Refund updated = refundRepository.save(refund);

    log.info("[RefundReject] refundId={} status=REJECTED adminComment={}", refundId, adminComment);

    return RefundResponseDto.from(updated);
  }

}
