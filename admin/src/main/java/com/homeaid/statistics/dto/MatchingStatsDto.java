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
public class MatchingStatsDto {

  @Schema(description = "연도", example = "2025")
  private int year;

  @Schema(description = "성공 매칭 수", example = "1000")
  private long successCount;

  @Schema(description = "실패/취소 매칭 수", example = "300")
  private long failCount;

}
