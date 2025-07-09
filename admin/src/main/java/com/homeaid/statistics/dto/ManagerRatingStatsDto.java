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
public class ManagerRatingStatsDto {

  @Schema(description = "연도", example = "2025")
  private int year;

  @Schema(description = "월 (선택, 1~12)", example = "6")
  private Integer month;

  @Schema(description = "일 (선택, 1~31)", example = "9")
  private Integer day;

  @Schema(description = "매니저 평균 평점", example = "4.52")
  private double avgRating;

  @Schema(description = "평점 수 (리뷰 수)", example = "345")
  private long reviewCount;
}
