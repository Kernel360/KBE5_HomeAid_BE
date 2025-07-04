package com.homeaid.payment.dto.response;

import com.homeaid.dto.response.ReservationResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "서비스/결제 통합 응답 DTO")
public class ReservationPaymentDetailResponseDto {

  @Schema(description = "서비스 예약 정보")
  private ReservationResponseDto reservation;

  @Schema(description = "결제 정보")
  private PaymentResponseDto payment;

  public static ReservationPaymentDetailResponseDto of(ReservationResponseDto reservation, PaymentResponseDto payment) {
    return ReservationPaymentDetailResponseDto.builder()
        .reservation(reservation)
        .payment(payment)
        .build();
  }

}
