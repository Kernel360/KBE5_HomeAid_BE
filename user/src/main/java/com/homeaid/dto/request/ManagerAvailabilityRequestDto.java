package com.homeaid.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerAvailabilityRequestDto {

  @Schema(description = "요일 (1=월요일 ~ 7=일요일)", example = "1")
  @Min(1)
  @Max(7)
  private int weekday;

  @Schema(description = "시작 가능 시간", example = "09:00")
  @NotNull
  private LocalTime startTime;

  @Schema(description = "종료 가능 시간", example = "18:00")
  @NotNull
  private LocalTime endTime;

  @Schema(description = "선호 지역 목록 (최대 3개)")
  @Size(min = 1, max = 3, message = "선호 지역은 1개 이상 3개 이하로 선택해야 합니다.")
  @Valid
  private List<ManagerPreferRegionRequest> preferRegions;

}