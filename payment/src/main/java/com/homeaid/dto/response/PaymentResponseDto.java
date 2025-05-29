package com.homeaid.dto.response;

import com.homeaid.domain.Payment;
import com.homeaid.domain.PaymentMethod;
import com.homeaid.domain.PaymentStatus;
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
