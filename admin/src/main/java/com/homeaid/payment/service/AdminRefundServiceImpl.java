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
import org.springframework.stereotype.Service;

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
  public RefundResponseDto approveRefund(Long refundId) {
    Refund refund = refundRepository.findById(refundId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.REFUND_NOT_FOUND));

    refund.getPayment().applyPartialRefund(refund.getRefundAmount());

    refund = refund.toBuilder()
        .status(RefundStatus.APPROVED)
        .processedAt(LocalDateTime.now())
        .build();

    Refund updated = refundRepository.save(refund);

    return toResponseDto(updated);
  }

  @Override
  @Transactional
  public RefundResponseDto rejectRefund(Long refundId) {
    Refund refund = refundRepository.findById(refundId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.REFUND_NOT_FOUND));

    refund = refund.toBuilder()
        .status(RefundStatus.REJECTED)
        .processedAt(LocalDateTime.now())
        .build();

    Refund updated = refundRepository.save(refund);

    return toResponseDto(updated);
  }

  private RefundResponseDto toResponseDto(Refund refund) {
    return RefundResponseDto.builder()
        .refundId(refund.getId())
        .paymentId(refund.getPayment().getId())
        .refundAmount(refund.getRefundAmount())
        .reason(refund.getReason())
        .status(refund.getStatus())
        .customerComment(refund.getCustomerComment())
        .requestedAt(refund.getRequestedAt())
        .processedAt(refund.getProcessedAt())
        .build();
  }
}
