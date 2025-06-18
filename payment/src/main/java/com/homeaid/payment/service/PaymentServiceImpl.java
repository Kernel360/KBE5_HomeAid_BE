package com.homeaid.payment.service;

import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.PaymentStatus;
import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.payment.dto.request.PaymentRequestDto;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.payment.exception.PaymentErrorCode;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final ReservationRepository reservationRepository;

  @Override
  @Transactional
  public PaymentResponseDto pay(PaymentRequestDto dto) { // 예약 상태 확인 후 결제
    // 1. 예약 조회
    Reservation reservation = reservationRepository.findById(dto.getReservationId())
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_RESERVATION_NOT_FOUND));

    // 2. 이미 해당 예약에 결제가 존재하는지 확인 (중복 결제 방지)
    boolean alreadyPaid = paymentRepository.existsByReservationId(dto.getReservationId());
    if (alreadyPaid) {
      throw new CustomException(PaymentErrorCode.PAYMENT_ALREADY_PAID);
    }

    // 3. 예약 상태 검증
    switch (reservation.getStatus()) {
      case MATCHED:
        break;
      case COMPLETED:
        throw new CustomException(PaymentErrorCode.PAYMENT_ALREADY_PAID);
      default:
        throw new CustomException(PaymentErrorCode.PAYMENT_INVALID_STATUS_FOR_PAYMENT);
    }

    // 4. 결제 생성 및 저장
    Payment payment = Payment.builder()
        .reservation(reservation)
        .amount(dto.getAmount())
        .paymentMethod(dto.getPaymentMethod())
        .status(PaymentStatus.PAID)
        .paidAt(LocalDateTime.now())
        .refundedAmount(0)
        .build();

    Payment saved = paymentRepository.save(payment);
    return PaymentResponseDto.toDto(saved);
  }

  @Override
  @Transactional
  public PaymentResponseDto customerCancelPayment(Long customerId, Long paymentId) {
    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

    // 본인 결제인지 검증
    if (!payment.getReservation().getCustomerId().equals(customerId)) {
      throw new CustomException(PaymentErrorCode.PAYMENT_ACCESS_DENIED);
    }

    // 예약 상태가 MATCHED 일 때만 취소 가능
    if (payment.getReservation().getStatus() != ReservationStatus.MATCHED) {
      throw new CustomException(PaymentErrorCode.PAYMENT_CANCELLATION_NOT_ALLOWED);
    }

    // 이미 환불되거나 취소된 경우 예외
    if (payment.getStatus() == PaymentStatus.REFUNDED || payment.getStatus() == PaymentStatus.CANCELED) {
      throw new CustomException(PaymentErrorCode.PAYMENT_ALREADY_REFUNDED);
    }

    payment.cancelPayment();
    return PaymentResponseDto.toDto(payment);
  }
  // 환불 중간에 문제가 발생하면 -> 전체 작업을 롤백해야 함 -> 트랜잭션이 없다면 중간 상태가 DB에 반영될 수 있음 -> 장애 위험 발생

  @Override
  public PaymentResponseDto getPayment(Long customerId, Long paymentId) {
    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

    if (!payment.getReservation().getCustomerId().equals(customerId)) {
      throw new CustomException(PaymentErrorCode.PAYMENT_ACCESS_DENIED);
    }
    return PaymentResponseDto.toDto(payment);
  }

  @Override
  public List<PaymentResponseDto> getAllPayments(Long customerId) {
    return paymentRepository.findByCustomerId(customerId)
        .stream().map(PaymentResponseDto::toDto).collect(Collectors.toList());
  }

}