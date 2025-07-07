package com.homeaid.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefundPartialFlexibleRequestDto {

  @Schema(description = "환불할 금액(원)", example = "30000")
  private Integer refundAmount;

  @Schema(description = "환불할 비율(%)", example = "50")
  private Integer refundPercentage;

}