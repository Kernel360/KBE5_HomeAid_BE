package com.homeaid.payment.dto.response;

import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.PaymentMethod;
import com.homeaid.payment.domain.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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

  @Schema(description = "결제 ID", example = "1")
  private Long id;

  @Schema(description = "예약 ID", example = "10")
  private Long reservationId;

  @Schema(description = "결제 금액", example = "50000")
  private Integer amount;

  @Schema(description = "결제 수단", example = "CARD")
  private PaymentMethod paymentMethod;

  @Schema(description = "결제 상태", example = "PAID")
  private PaymentStatus status;

  @Schema(description = "결제 일시", example = "2025-06-19T14:00:00")
  private LocalDateTime paidAt;

  @Schema(description = "고객 이름", example = "박호일")
  private String customerName;

  public static PaymentResponseDto toDto(Payment payment, String customerName) {
    return PaymentResponseDto.builder()
        .id(payment.getId())
        .reservationId(payment.getReservation().getId())
        .amount(payment.getAmount())
        .paymentMethod(payment.getPaymentMethod())
        .status(payment.getStatus())
        .paidAt(payment.getPaidAt())
        .customerName(customerName)
        .build();
  }
}
