package com.homeaid.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "매니저 상세정보 등록 DTO")
public class ManagerDetailInfoRequestDto {

  @Schema(description = "선호 기능 ID 목록", example = "[1, 2, 3]")
  @NotEmpty
  private List<Long> preferenceIds;

  @Schema(description = "가능한 요일 (1~7, 월~일)", example = "[1, 3, 5]")
  @NotEmpty
  private List<Integer> availableDays;

  @Schema(description = "위도", example = "13.7563")
  @NotNull
  private Double latitude;

  @Schema(description = "경도", example = "100.5018")
  @NotNull
  private Double longitude;

  @Schema(description = "시작 가능 시간", example = "09:00")
  @NotNull
  private LocalTime startTime;

  @Schema(description = "종료 가능 시간", example = "18:00")
  @NotNull
  private LocalTime endTime;

}