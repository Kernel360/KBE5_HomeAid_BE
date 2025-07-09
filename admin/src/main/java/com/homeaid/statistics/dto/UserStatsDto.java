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
public class UserStatsDto {

  @Schema(description = "해당 연도", example = "2025")
  private int year;

  @Schema(description = "월 (선택, 1~12)", example = "6")
  private Integer month;

  @Schema(description = "일 (선택, 1~31)", example = "9")
  private Integer day;

  @Schema(description = "가입자 수", example = "1234")
  private long signupCount;

  @Schema(description = "누적 가입자 수", example = "5211")
  private long totalUsers;

  @Schema(description = "탈퇴 회원 수", example = "120")
  private long withdrawCount;

}
