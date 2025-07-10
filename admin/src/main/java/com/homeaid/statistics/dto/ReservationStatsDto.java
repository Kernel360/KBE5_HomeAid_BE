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
public class ReservationStatsDto {

  @Schema(description = "연도", example = "2025")
  private int year;

  @Schema(description = "월 (선택, 1~12)", example = "6")
  private Integer month;

  @Schema(description = "일 (선택, 1~31)", example = "9")
  private Integer day;

  @Schema(description = "총 예약 수", example = "1800")
  private long reservationCount;

  @Schema(description = "예약 취소 수", example = "123")
  private long cancelledCount;

  @Schema(description = "예약 평균 처리 시간 (분 단위)", example = "15")
  private double avgProcessingMinutes;

  @Schema(description = "예약 성공률 (%)", example = "87.5")
  private double reservationSuccessRate;

}
