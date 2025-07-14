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

  @Schema(description = "총 환불 금액")
  private Integer totalRefundedAmount;

  @Schema(description = "총 실 결제 금액")
  private Integer totalNetAmount;

  public static SettlementPaymentDetailResponseDto of(List<PaymentResponseDto> payments) {
    int total = 0;
    int refunded = 0;
    int net = 0;

    for (PaymentResponseDto p : payments) {
      total += p.getAmount() != null ? p.getAmount() : 0;
      refunded += p.getRefundedAmount() != null ? p.getRefundedAmount() : 0;
      net += p.getNetAmount() != null ? p.getNetAmount() : 0;
    }

    return SettlementPaymentDetailResponseDto.builder()
        .payments(payments)
        .totalAmount(total)
        .totalRefundedAmount(refunded)
        .totalNetAmount(net)
        .build();
  }
}
