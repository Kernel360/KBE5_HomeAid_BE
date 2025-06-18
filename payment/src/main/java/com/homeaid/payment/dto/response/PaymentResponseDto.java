package com.homeaid.payment.dto.response;

import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.PaymentMethod;
import com.homeaid.payment.domain.PaymentStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {

  private Long id;
  private Long reservationId;
  private Integer amount;
  private PaymentMethod paymentMethod;
  private PaymentStatus status;
  private LocalDateTime paidAt;

  public static PaymentResponseDto toDto(Payment payment) {
    return PaymentResponseDto.builder()
        .id(payment.getId())
        .reservationId(payment.getReservation().getId())
        .amount(payment.getAmount())
        .paymentMethod(payment.getPaymentMethod())
        .status(payment.getStatus())
        .paidAt(payment.getPaidAt())
        .build();
  }
}
