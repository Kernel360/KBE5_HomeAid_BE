package com.homeaid.payment.service;

import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.exception.CustomException;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.Refund;
import com.homeaid.payment.domain.enumerate.RefundStatus;
import com.homeaid.payment.dto.request.RefundPartialFlexibleRequestDto;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.payment.dto.response.RefundResponseDto;
import com.homeaid.payment.exception.PaymentErrorCode;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.payment.repository.RefundRepository;
import com.homeaid.payment.validator.RefundValidator;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  public Page<RefundResponseDto> getAllRefunds(Pageable pageable) {
    return refundRepository.findAll(pageable).map(RefundResponseDto::from);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<RefundResponseDto> getRefundsByUserId(Long userId, Pageable pageable) {
    return refundRepository.findByPayment_Reservation_CustomerId(userId, pageable)
        .map(RefundResponseDto::from);
  }

  @Override
  @Transactional(readOnly = true)
  public RefundResponseDto getRefundDetail(Long refundId) {
    Refund refund = refundValidator.getRefundOrThrow(refundId);
    return RefundResponseDto.from(refund);
  }


  // 관리자가 수동으로 결제 전체 금액을 전액 환불
  @Override
  @Transactional
  public PaymentResponseDto refundFull(Long paymentId) {
    Payment payment = getPaymentOrThrow(paymentId);
    payment.refund(payment.getReservation().getStatus());
    return adminPaymentService.getPayment(paymentId);
  }

  @Override
  @Transactional
  public PaymentResponseDto refundPartial(Long paymentId, RefundPartialFlexibleRequestDto request) {
    Payment payment = getPaymentOrThrow(paymentId);
    ReservationStatus reservationStatus = payment.getReservation().getStatus();
    int calculatedAmount = calculateRefundAmount(payment, request);

    validateMaxRefundForCompleted(reservationStatus, payment, calculatedAmount);

    payment.applyPartialRefund(calculatedAmount);
    log.info("[관리자 부분환불] paymentId={} refundAmount={} reservationStatus={}", paymentId, calculatedAmount, reservationStatus);

    return adminPaymentService.getPayment(paymentId);
  } // 관리자가 직접 부분환불 (임의로 금액, % 지정 후) - 고객 환불 요청 프로세스를 거치지 않고, 관리자가 주도적으로 즉시 환불하는 시나리오

  private int calculateRefundAmount(Payment payment, RefundPartialFlexibleRequestDto request) {
    if (request.getRefundPercentage() != null) {
      validatePercentage(request.getRefundPercentage());
      return payment.getAmount() * request.getRefundPercentage() / 100;
    } else if (request.getRefundAmount() != null) {
      validateRefundAmount(request.getRefundAmount());
      return request.getRefundAmount();
    } else {
      throw new CustomException(PaymentErrorCode.MISSING_REFUND_INPUT);
    }
  }

  private void validatePercentage(Integer percentage) {
    if (percentage <= 0 || percentage > 100) {
      throw new CustomException(PaymentErrorCode.INVALID_REFUND_PERCENTAGE);
    }
  }

  private void validateRefundAmount(Integer amount) {
    if (amount <= 0) {
      throw new CustomException(PaymentErrorCode.INVALID_REFUND_AMOUNT);
    }
  }

  private void validateMaxRefundForCompleted(ReservationStatus status, Payment payment, int calculatedAmount) {
    if (status == ReservationStatus.COMPLETED) {
      int maxAllowed = payment.getAmount() / 2;
      if (calculatedAmount > maxAllowed) {
        throw new CustomException(PaymentErrorCode.REFUND_AMOUNT_EXCEEDS_ALLOWED_PERCENTAGE);
      }
    }
  }

  private Payment getPaymentOrThrow(Long paymentId) {
    return paymentRepository.findById(paymentId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));
  }


  // 고객이 요청시 사용
  @Override
  @Transactional
  public RefundResponseDto approveRefund(Long refundId, String adminComment) {
    Refund refund = refundValidator.getRefundOrThrow(refundId);
    refundValidator.validateRefundStatusIsRequest(refund);

    refund.getPayment().applyPartialRefund(refund.getRefundAmount());
    refund = refund.toBuilder()
        .status(RefundStatus.APPROVED)
        .adminComment(adminComment)
        .processedAt(LocalDateTime.now())
        .build();

    Refund updated = refundRepository.save(refund);
    log.info("[환불 승인] refundId={} status=APPROVED adminComment={}", refundId, adminComment);
    return RefundResponseDto.from(updated);
  }

  @Override
  @Transactional
  public RefundResponseDto rejectRefund(Long refundId, String adminComment) {
    Refund refund = refundValidator.getRefundOrThrow(refundId);
    refundValidator.validateRefundStatusIsRequest(refund);

    refund = refund.toBuilder()
        .status(RefundStatus.REJECTED)
        .adminComment(adminComment)
        .processedAt(LocalDateTime.now())
        .build();

    Refund updated = refundRepository.save(refund);
    log.info("[환불 거절] refundId={} status=REJECTED adminComment={}", refundId, adminComment);
    return RefundResponseDto.from(updated);
  }

}
