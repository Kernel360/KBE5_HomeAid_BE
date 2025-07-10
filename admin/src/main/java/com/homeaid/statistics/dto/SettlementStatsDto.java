package com.homeaid.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementStatsDto {

  @Schema(description = "연도", example = "2025")
  private int year;

  @Schema(description = "월 (선택, 1~12)", example = "6")
  private Integer month;

  @Schema(description = "일 (선택, 1~31)", example = "9")
  private Integer day;

  @Schema(description = "정산 신청 수", example = "300")
  private long requestedCount;

  @Schema(description = "정산 지급 완료 수", example = "280")
  private long paidCount;
}
