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
public class SettlementStatsDto {

  @Schema(description = "연도", example = "2025")
  private int year;
  @Schema(description = "정산 신청 수", example = "300")
  private long requestedCount;

  @Schema(description = "정산 지급 완료 수", example = "280")
  private long paidCount;
}
