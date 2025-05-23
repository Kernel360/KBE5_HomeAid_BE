package com.example.homeaid.payment.service;

import com.example.homeaid.payment.dto.PaymentRequestDto;
import com.example.homeaid.payment.dto.PaymentResponseDto;
import com.example.homeaid.payment.entity.Payment;
import com.example.homeaid.payment.entity.PaymentStatus;
import com.example.homeaid.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  //private final ReservationRepository reservationRepository;

  @Override
  @Transactional
  public PaymentResponseDto pay(PaymentRequestDto dto) {
    // 1. 예약 조회
//    Reservation reservation = reservationRepository.findById(dto.getReservationId())
//        .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));

    // 2. 예약 상태가 COMPLETED인지 확인
//    if (reservation.getStatus() != ReservationStatus.COMPLETED) {
//      throw new IllegalStateException("예약 상태가 COMPLETED일 때만 결제할 수 있습니다.");
//    }

    // 3. 결제 생성 및 저장
    Payment payment = Payment.builder()
        .reservationId(dto.getReservationId())
        .amount(dto.getAmount())
        .paymentMethod(dto.getPaymentMethod())
        .status(PaymentStatus.PAID)
        .paidAt(LocalDateTime.now())
        .build();

    Payment saved = paymentRepository.save(payment);

    return PaymentResponseDto.from(saved);
  }
}
