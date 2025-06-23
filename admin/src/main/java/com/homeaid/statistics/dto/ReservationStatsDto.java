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
public class ReservationStatsDto {

  @Schema(description = "연도", example = "2025")
  private int year;

  @Schema(description = "총 예약 수", example = "1800")
  private long reservationCount;

  @Schema(description = "예약 평균 처리 시간 (분 단위)", example = "15")
  private double avgProcessingMinutes;
}
