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
  @Schema(description = "가입자 수", example = "1234")
  private long signupCount;

  @Schema(description = "누적 가입자 수", example = "5211")
  private long totalUsers;

  @Schema(description = "탈퇴 회원 수", example = "120")
  private long withdrawCount;

  @Schema(description = "마지막 로그인 6개월 이상 미접속 회원 수", example = "300")
  private long inactiveUserCount;

  @Schema(description = "탈퇴율 (퍼센트)", example = "13.5")
  private double withdrawRate;

  @Schema(description = "휴면자 수", example = "90")
  private long dormantCount;
}
