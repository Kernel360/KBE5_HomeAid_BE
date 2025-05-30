package com.homeaid.service;

import com.homeaid.domain.Payment;
import com.homeaid.domain.PaymentStatus;
import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.dto.request.PaymentRequestDto;
import com.homeaid.dto.response.PaymentResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.PaymentErrorCode;
import com.homeaid.repository.PaymentRepository;
import com.homeaid.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final ReservationRepository reservationRepository;

  @Override
  @Transactional
  public PaymentResponseDto pay(PaymentRequestDto dto) {
    Reservation reservation = reservationRepository.findById(dto.getReservationId())
        .orElseThrow(() -> new CustomException(
            PaymentErrorCode.PAYMENT_RESERVATION_NOT_FOUND));

    boolean alreadyPaid = paymentRepository.existsByReservationIdAndStatus(reservation.getId(),
        PaymentStatus.PAID);
    if (alreadyPaid) {
      throw new CustomException(PaymentErrorCode.PAYMENT_ALREADY_PAID);
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
