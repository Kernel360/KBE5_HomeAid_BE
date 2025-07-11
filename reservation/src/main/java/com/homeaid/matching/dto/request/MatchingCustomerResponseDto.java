package com.homeaid.matching.dto.request;

import com.homeaid.matching.validation.MatchingActionMemoRequest;
import com.homeaid.matching.validation.MatchingMemoConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "고객 매칭 응답 DTO")
@MatchingMemoConstraint
public class MatchingCustomerResponseDto implements MatchingActionMemoRequest {

  @NotNull
  @Schema(description = "고객의 응답 액션", example = "CONFIRM")
  private CustomerAction action;

  @Schema(description = "거절 사유 메모 (거절 시 선택)", example = "다른 매니저를 원합니다.")
  private String memo;

  @Schema(description = "고객의 응답 액션")
  public enum CustomerAction {
    CONFIRM,
    REJECT
  }

}
