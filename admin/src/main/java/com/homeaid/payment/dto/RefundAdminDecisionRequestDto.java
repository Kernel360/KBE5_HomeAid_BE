package com.homeaid.payment.dto;

import com.homeaid.payment.domain.enumerate.RefundReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefundAdminDecisionRequestDto {

  @NotNull(message = "환불 사유는 필수입니다.")
  private RefundReason refundReason;

  @NotBlank(message = "관리자 코멘트는 필수입니다.")
  private String adminComment;

  @Schema(description = "환불할 금액(원)", example = "30000")
  private Integer refundAmount;

  @Schema(description = "환불할 비율(%)", example = "50")
  private Integer refundPercentage;
}