package com.homeaid.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "탈퇴 승인/거절 요청 DTO")
public class AdminWithdrawalDecisionDto {

  @Schema(description = "탈퇴 승인 여부 (true = 승인, false = 거절)", example = "true")
  private boolean approve;
}