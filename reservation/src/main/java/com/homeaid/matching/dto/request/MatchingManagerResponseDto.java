package com.homeaid.matching.dto.request;

import com.homeaid.matching.validation.MatchingActionMemoRequest;
import com.homeaid.matching.validation.MatchingMemoConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "매니저 매칭 응답 DTO")
@MatchingMemoConstraint
public class MatchingManagerResponseDto implements MatchingActionMemoRequest {

  @NotNull
  @Schema(description = "매니저의 응답 액션", example = "ACCEPT")
  private ManagerAction action;

  @Schema(description = "거절 사유 메모 (거절 시 필수)", example = "일정이 맞지 않습니다.")
  private String memo;

  @Schema(description = "매니저의 응답 액션")
  public enum ManagerAction {
    ACCEPT,
    REJECT
  }

}
