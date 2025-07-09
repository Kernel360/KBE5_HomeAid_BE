package com.homeaid.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatsDto {

  @Schema(description = "연도", example = "2025")
  private int year;

  @Schema(description = "월 (선택, 1~12)", example = "6")
  private Integer month;

  @Schema(description = "일 (선택, 1~31)", example = "9")
  private Integer day;

  @Schema(description = "총 결제 금액", example = "15000000")
  private long totalAmount;

  @Schema(description = "취소 금액", example = "2000000")
  private long cancelAmount;

  @Schema(description = "결제 건수", example = "370")
  private long paymentCount;

  @Schema(description = "취소 건수", example = "12")
  private long cancelCount;

  @Schema(description = "카드 결제 금액", example = "1000000")
  private long card;

  @Schema(description = "계좌이체 결제 금액", example = "200000")
  private long transfer;

  @Schema(description = "현금 결제 금액", example = "50000")
  private long cash;
}
