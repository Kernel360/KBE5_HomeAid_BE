package com.homeaid.statistics.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatisticsRequestDto {

  @Min(value = 2000, message = "연도는 2000년 이후여야 합니다.")
  @Max(value = 2100, message = "연도는 2100년 이하여야 합니다.")
  private int year;

  @Min(value = 1, message = "월은 1 이상이어야 합니다.")
  @Max(value = 12, message = "월은 12 이하여야 합니다.")
  private Integer month;

  @Min(value = 1, message = "일은 1 이상이어야 합니다.")
  @Max(value = 31, message = "일은 31 이하여야 합니다.")
  private Integer day;

}
