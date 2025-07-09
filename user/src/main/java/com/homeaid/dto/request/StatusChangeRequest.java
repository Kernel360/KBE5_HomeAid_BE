package com.homeaid.dto.request;

import com.homeaid.domain.enumerate.ManagerStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Schema(description = "매니저 상태 변경 요청 DTO")
public class StatusChangeRequest {

  @Schema(
      description = "변경할 매니저 상태",
      example = "ACTIVE",
      required = true
  )
  private ManagerStatus status;
}

