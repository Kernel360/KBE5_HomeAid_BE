package com.homeaid.service;

import com.homeaid.domain.Payment;
import com.homeaid.domain.PaymentStatus;
import com.homeaid.domain.Reservation;
import com.homeaid.dto.request.PaymentRequestDto;
import com.homeaid.dto.response.PaymentResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.PaymentErrorCode;
import com.homeaid.repository.PaymentRepository;
import com.homeaid.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final ReservationRepository reservationRepository;

  @Transactional
  public PaymentResponseDto pay(PaymentRequestDto dto) {

    // 예약 조회
    Reservation reservation = reservationRepository.findById(dto.getReservationId())
        .orElseThrow(() -> new CustomException(
            PaymentErrorCode.PAYMENT_RESERVATION_NOT_FOUND));

    // 중복 결제 방지: 이미 결제된 Payment 가 있으면 예외
    boolean alreadyPaid = paymentRepository.existsByReservationIdAndStatus(reservation.getId(),
        PaymentStatus.PAID);
    if (alreadyPaid) {
      throw new CustomException(PaymentErrorCode.PAYMENT_ALREADY_PAID);
    }

    // total_price 가 Integer, dto.getAmount()가 BigDecimal 라서 변환 후 결제 금액 비교
    if (reservation.getTotalPrice() == null ||
        dto.getAmount() == null ||
        new BigDecimal(reservation.getTotalPrice()).compareTo(dto.getAmount()) != 0) {
      throw new CustomException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
    }

    // 결제 처리
    Payment payment = Payment.builder()
        .reservation(reservation)
        .amount(dto.getAmount())
        .paymentMethod(dto.getPaymentMethod())
        .status(PaymentStatus.PAID)
        .paidAt(LocalDateTime.now())
        .build();

    Payment saved = paymentRepository.save(payment);

    return PaymentResponseDto.toDto(saved);

  }
}
