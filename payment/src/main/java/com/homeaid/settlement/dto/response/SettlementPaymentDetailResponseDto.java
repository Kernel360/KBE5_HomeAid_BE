package com.homeaid.settlement.dto.response;

import com.homeaid.payment.dto.response.PaymentResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "정산 결제 상세 + 합계 응답 DTO")
public class SettlementPaymentDetailResponseDto {

  @Schema(description = "결제 내역 리스트")
  private List<PaymentResponseDto> payments;

  @Schema(description = "총 결제 금액")
  private Integer totalAmount;

  public static SettlementPaymentDetailResponseDto of(List<PaymentResponseDto> payments) {
    int sum = payments.stream()
        .mapToInt(PaymentResponseDto::getAmount)
        .sum();

    return SettlementPaymentDetailResponseDto.builder()
        .payments(payments)
        .totalAmount(sum)
        .build();
  }

}
