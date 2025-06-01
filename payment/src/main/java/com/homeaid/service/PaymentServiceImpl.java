package com.homeaid.service;

import com.homeaid.domain.Payment;
import com.homeaid.domain.PaymentStatus;
import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.dto.request.PaymentRequestDto;
import com.homeaid.dto.response.PaymentResponseDto;
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
  public PaymentResponseDto pay(PaymentRequestDto dto) { // 예약 상태 확인 후 결제
    // 1. 예약 조회
    Reservation reservation = reservationRepository.findById(dto.getReservationId())
        .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));

    // 2. 예약 상태가 COMPLETED 인지 확인
    if (reservation.getStatus() != ReservationStatus.COMPLETED) {
      throw new IllegalStateException("예약 상태가 COMPLETED 일 때만 결제할 수 있습니다.");
    }

    // 3. 결제 생성 및 저장
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
