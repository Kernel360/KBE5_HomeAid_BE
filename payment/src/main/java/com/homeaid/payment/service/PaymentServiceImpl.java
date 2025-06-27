package com.homeaid.payment.service;

import com.homeaid.domain.User;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.PaymentStatus;
import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.payment.dto.request.PaymentRequestDto;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.payment.exception.PaymentErrorCode;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.payment.validator.PaymentValidator;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.repository.UserRepository;
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
  private final UserRepository userRepository;
  private final PaymentValidator paymentValidator;

  @Override
  @Transactional
  public PaymentResponseDto pay(PaymentRequestDto dto) { // 예약 상태 확인 후 결제

    Reservation reservation = reservationRepository.findById(dto.getReservationId())
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_RESERVATION_NOT_FOUND));

    paymentValidator.validateNotAlreadyPaid(reservation.getId());
    paymentValidator.validateReservableStatus(reservation.getStatus());

    Payment payment = Payment.builder()
        .reservation(reservation)
        .amount(dto.getAmount())
        .paymentMethod(dto.getPaymentMethod())
        .status(PaymentStatus.PAID)
        .paidAt(LocalDateTime.now())
        .refundedAmount(0)
        .build();

    return toDtoWithNames(paymentRepository.save(payment));
  }

  @Override
  @Transactional
  public PaymentResponseDto customerCancelPayment(Long customerId, Long paymentId) {
    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

    paymentValidator.validatePaymentOwnership(payment, customerId);

    payment.cancel(payment.getReservation().getStatus());

    return toDtoWithNames(payment);
  }
  // 환불 중간에 문제가 발생하면 -> 전체 작업을 롤백해야 함 -> 트랜잭션이 없다면 중간 상태가 DB에 반영될 수 있음 -> 장애 위험 발생

  // 결제 단건 조회
  @Override
  public PaymentResponseDto getPayment(Long customerId, Long paymentId) {
    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

    if (!payment.getReservation().getCustomerId().equals(customerId)) {
      throw new CustomException(PaymentErrorCode.PAYMENT_ACCESS_DENIED);
    }

    return toDtoWithNames(payment);
  }

  // 전체 결제 조회
  @Override
  public List<PaymentResponseDto> getAllPayments(Long customerId) {
    return paymentRepository.findByCustomerId(customerId).stream()
        .map(this::toDtoWithNames)
        .collect(Collectors.toList());
  }

  // 공통 DTO 변환 메서드 (이름 포함)
  private PaymentResponseDto toDtoWithNames(Payment payment) {
    Reservation reservation = payment.getReservation();

    String customerName = userRepository.findById(reservation.getCustomerId())
        .map(User::getName)
        .orElse("알 수 없음");

    return PaymentResponseDto.toDto(payment, customerName);
  }
}