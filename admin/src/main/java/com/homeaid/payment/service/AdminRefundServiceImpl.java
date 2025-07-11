package com.homeaid.payment.service;


import com.homeaid.exception.CustomException;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.Refund;
import com.homeaid.payment.domain.enumerate.RefundStatus;
import com.homeaid.payment.domain.factory.RefundFactory;
import com.homeaid.payment.dto.RefundAdminDecisionRequestDto;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.payment.dto.response.RefundResponseDto;
import com.homeaid.payment.exception.PaymentErrorCode;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.payment.repository.RefundRepository;
import com.homeaid.payment.validator.RefundValidator;
import com.homeaid.reservation.domain.enumerate.ReservationStatus;
import java.util.List;
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
  public PaymentResponseDto refundFull(Long paymentId, RefundAdminDecisionRequestDto dto) {
    Payment payment = getPaymentOrThrow(paymentId);
    validateNoDuplicateRefund(payment);
    ReservationStatus reservationStatus = payment.getReservation().getStatus();

    payment.refund(reservationStatus);

    Refund refund = RefundFactory.createManualRefund(
        payment,
        payment.getAmount(),
        dto.getRefundReason(),
        dto.getAdminComment()
    );

    refundRepository.save(refund);

    //log.info("[관리자 전액환불 기록] refundId={} paymentId={}", refund.getId(), paymentId);

    return adminPaymentService.getPayment(paymentId);
  }

  @Override
  @Transactional
  public PaymentResponseDto refundPartial(Long paymentId, RefundAdminDecisionRequestDto dto) {
    Payment payment = getPaymentOrThrow(paymentId);
    validateNoDuplicateRefund(payment);
    ReservationStatus reservationStatus = payment.getReservation().getStatus();

    int calculatedAmount = calculateRefundAmount(payment, dto);
    validateMaxRefundForCompleted(reservationStatus, payment, calculatedAmount);

    payment.applyPartialRefund(calculatedAmount);

    Refund refund = RefundFactory.createManualRefund(
        payment,
        calculatedAmount,
        dto.getRefundReason(),
        dto.getAdminComment()
    );

    refundRepository.save(refund);

    //log.info("[관리자 부분환불 기록] refundId={} paymentId={} refundAmount={}", refund.getId(), paymentId, calculatedAmount);

    return adminPaymentService.getPayment(paymentId);
  } // 관리자가 직접 부분환불 (임의로 금액, % 지정 후) - 고객 환불 요청 프로세스를 거치지 않고, 관리자가 주도적으로 즉시 환불하는 시나리오


  // 고객이 요청시 사용
  @Override
  @Transactional
  public RefundResponseDto approveRefund(Long refundId, String adminComment) {
    Refund refund = refundValidator.getRefundOrThrow(refundId);
    refundValidator.validateRefundStatusIsRequest(refund);

    refund.getPayment().applyPartialRefund(refund.getRefundAmount());

    Refund approved = refund.approve(adminComment);  // 도메인 로직 활용
    refundRepository.save(approved);

    log.info("[환불 승인] refundId={} status=APPROVED", refundId);
    return RefundResponseDto.from(approved);
  }

  @Override
  @Transactional
  public RefundResponseDto rejectRefund(Long refundId, String adminComment) {
    Refund refund = refundValidator.getRefundOrThrow(refundId);
    refundValidator.validateRefundStatusIsRequest(refund);

    Refund rejected = refund.reject(adminComment);  // 도메인 로직 활용
    refundRepository.save(rejected);

    log.info("[환불 거절] refundId={} status=REJECTED", refundId);
    return RefundResponseDto.from(rejected);
  }


  private int calculateRefundAmount(Payment payment, RefundAdminDecisionRequestDto request) {
    if (request.getRefundPercentage() != null) {
      validatePercentage(request.getRefundPercentage());
      return payment.getAmount() * request.getRefundPercentage() / 100;
    } else if (request.getRefundAmount() != null) {
      validateRefundAmount(request.getRefundAmount());
      return request.getRefundAmount();
    } else {
      throw new CustomException(PaymentErrorCode.MISSING_REFUND_INPUT);
    }
  } //  환불 금액 계산 - 환불 비율이 우선, 없으면 환불 금액으로 처리

  private void validatePercentage(Integer percentage) {
    if (percentage <= 0 || percentage > 100) {
      throw new CustomException(PaymentErrorCode.INVALID_REFUND_PERCENTAGE);
    }
  } // 환불 비율 검증 (1~100%)

  private void validateRefundAmount(Integer amount) {
    if (amount <= 0) {
      throw new CustomException(PaymentErrorCode.INVALID_REFUND_AMOUNT);
    }
  } // 환불 금액 검증 (0원 초과)

  private void validateMaxRefundForCompleted(ReservationStatus status, Payment payment, int calculatedAmount) {
    if (status == ReservationStatus.COMPLETED) {
      int maxAllowed = payment.getAmount() / 2;
      if (calculatedAmount > maxAllowed) {
        throw new CustomException(PaymentErrorCode.REFUND_AMOUNT_EXCEEDS_ALLOWED_PERCENTAGE);
      }
    }
  } // 예약이 서비스 완료 상태라면 환불 가능한 최대 금액을 초과하지 않는지 검증

  private Payment getPaymentOrThrow(Long paymentId) {
    return paymentRepository.findById(paymentId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));
  } // Payment 엔티티 조회 및 예외 처리

  private void validateNoDuplicateRefund(Payment payment) {
    if (payment.getRefundedAmount() >= payment.getAmount()) {
      throw new CustomException(PaymentErrorCode.ALREADY_FULLY_REFUNDED);
    }
    boolean hasApprovedRefund = refundRepository.existsByPayment_IdAndStatusIn(
        payment.getId(), List.of(RefundStatus.APPROVED, RefundStatus.COMPLETED));
    if (hasApprovedRefund) {
      throw new CustomException(PaymentErrorCode.ALREADY_HAS_APPROVED_REFUND);
    }
  } // 중복 환불 방지 검증 메서드 - 이미 전액 환불된 결제 또는 승인/완료된 환불 기록이 있으면 예외 발생

}
