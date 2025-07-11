package com.homeaid.matching.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "매칭 생성 요청 DTO")
public class CreateMatchingRequestDto {

  @NotNull
  @Schema(description = "예약 ID", example = "1")
  private Long reservationId;

  @NotNull
  @Schema(description = "매니저 ID", example = "2")
  private Long managerId;


}
