package com.homeaid.payment.service;

import com.homeaid.exception.CustomException;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.Refund;
import com.homeaid.payment.domain.enumerate.RefundStatus;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.payment.dto.response.RefundResponseDto;
import com.homeaid.payment.exception.PaymentErrorCode;
import com.homeaid.payment.exception.RefundErrorCode;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.payment.repository.RefundRepository;
import com.homeaid.payment.validator.RefundValidator;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminRefundServiceImpl implements AdminRefundService {

  private final PaymentRepository paymentRepository;
  private final AdminPaymentService adminPaymentService;
  private final RefundRepository refundRepository;
  private final RefundValidator refundValidator;

  @Override
  @Transactional(readOnly = true)
  public PaymentResponseDto refundFull(Long paymentId) {
    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

    payment.refund(payment.getReservation().getStatus());

    return adminPaymentService.getPayment(paymentId);
  }

  @Override
  @Transactional(readOnly = true)
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
        .orElseThrow(() -> new CustomException(RefundErrorCode.REFUND_NOT_FOUND));

    refundValidator.validateRefundStatusIsRequest(refund);

    refund.getPayment().applyPartialRefund(refund.getRefundAmount());
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
        .orElseThrow(() -> new CustomException(RefundErrorCode.REFUND_NOT_FOUND));

    refundValidator.validateRefundStatusIsRequest(refund);

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
