package com.homeaid.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.homeaid.domain.Payment;
import com.homeaid.domain.PaymentMethod;
import com.homeaid.domain.PaymentStatus;
import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.dto.request.PaymentRequestDto;
import com.homeaid.dto.response.PaymentResponseDto;
import com.homeaid.repository.PaymentRepository;
import com.homeaid.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.test.util.ReflectionTestUtils;

class PaymentServiceImplTest {

  @Mock
  private PaymentRepository paymentRepository;
  @Mock
  private ReservationRepository reservationRepository;

  @InjectMocks
  private PaymentServiceImpl paymentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("pay: 예약 상태가 COMPLETED면 결제 성공")
  void pay_success() {
    // given
    Long reservationId = 1L;

    // 빌더로 예약 객체를 만들고, id는 ReflectionTestUtils로 강제 세팅!
    Reservation reservation = Reservation.builder()
        //.status(ReservationStatus.COMPLETED)
        .build();
    ReflectionTestUtils.setField(reservation, "id", reservationId);
    ReflectionTestUtils.setField(reservation, "status", ReservationStatus.COMPLETED);

    PaymentRequestDto dto = PaymentRequestDto.builder()
        .reservationId(reservationId)
        .amount(15000)
        .paymentMethod(PaymentMethod.CARD)
        .build();

    Payment payment = Payment.builder()
        .reservation(reservation)
        .amount(dto.getAmount())
        .paymentMethod(dto.getPaymentMethod())
        .status(PaymentStatus.PAID)
        .paidAt(LocalDateTime.now())
        .build();

    given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
    given(paymentRepository.save(any(Payment.class))).willReturn(payment);

    // when
    PaymentResponseDto result = paymentService.pay(dto);

    // then
    assertThat(result.getReservationId()).isEqualTo(reservationId);
    assertThat(result.getAmount()).isEqualTo(15000);
    assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.CARD);
  }


  @Test
  @DisplayName("pay: 예약이 존재하지 않으면 예외")
  void pay_reservationNotFound() {
    // given
    Long reservationId = 1L;
    PaymentRequestDto dto = PaymentRequestDto.builder()
        .reservationId(reservationId)
        .amount(10000)
        .paymentMethod(PaymentMethod.CARD)
        .build();

    given(reservationRepository.findById(reservationId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> paymentService.pay(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("예약이 존재하지 않습니다.");
  }

  @Test
  @DisplayName("pay: 예약 상태가 COMPLETED가 아니면 예외")
  void pay_reservationNotCompleted() {
    // given
    Long reservationId = 3L;

    // 빌더로 예약 객체를 만들고, id는 ReflectionTestUtils로 강제 세팅!
    Reservation reservation = Reservation.builder()
        //.status(ReservationStatus.COMPLETED)
        .build();
    ReflectionTestUtils.setField(reservation, "id", reservationId);
    ReflectionTestUtils.setField(reservation, "status", ReservationStatus.REQUESTED);

    PaymentRequestDto dto = PaymentRequestDto.builder()
        .reservationId(reservationId)
        .amount(20000)
        .paymentMethod(PaymentMethod.CARD)
        .build();

    given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));

    // when & then
    assertThatThrownBy(() -> paymentService.pay(dto))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("예약 상태가 COMPLETED 일 때만 결제할 수 있습니다.");
  }
}
