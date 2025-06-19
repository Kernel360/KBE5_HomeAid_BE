package com.homeaid.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "매니저 상세정보 등록 DTO")
public class ManagerDetailInfoRequestDto {

  @Schema(description = "근무 가능 요일별 설정 리스트")
  @NotEmpty(message = "최소 1개의 요일 설정이 필요합니다.")
  @Valid
  private List<ManagerAvailabilityRequestDto> availabilities;

}