package com.homeaid.user.dto.request;

import com.homeaid.domain.enumerate.ManagerStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AdminDocumentReviewRequest { // 매니저 파일 업로드 관리

  @NotNull
  @Schema(description = "매니저 상태 (APPROVED, REJECTED)", example = "REJECTED")
  private ManagerStatus status;

  @Schema(description = "반려 사유", example = "파일이 명확하지 않습니다.")
  private String rejectionReason;
}
