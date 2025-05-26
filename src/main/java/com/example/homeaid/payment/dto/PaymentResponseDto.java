package com.example.homeaid.payment.dto;

import com.example.homeaid.payment.entity.Payment;
import com.example.homeaid.payment.entity.PaymentMethod;
import com.example.homeaid.payment.entity.PaymentStatus;
import java.math.BigDecimal;
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
  private BigDecimal amount;
  private PaymentMethod paymentMethod;
  private PaymentStatus status;
  private LocalDateTime paidAt;

  public static PaymentResponseDto from(Payment payment) {
    return PaymentResponseDto.builder()
        .id(payment.getId())
        .reservationId(payment.getReservationId())
        .amount(payment.getAmount())
        .paymentMethod(payment.getPaymentMethod())
        .status(payment.getStatus())
        .paidAt(payment.getPaidAt())
        .build();
  }
}
