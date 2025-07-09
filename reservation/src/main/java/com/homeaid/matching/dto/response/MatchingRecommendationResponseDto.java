package com.homeaid.matching.dto.response;

import com.homeaid.domain.Manager;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "매칭 추천 결과 DTO")
public class MatchingRecommendationResponseDto {

  @Schema(description = "매니저 ID", example = "101")
  private Long managerId;

  @Schema(description = "매니저 이름", example = "김매니저")
  private String managerName;

  public static MatchingRecommendationResponseDto toDto(Manager manager) {
    return MatchingRecommendationResponseDto.builder().managerId(manager.getId())
        .managerName(manager.getName()).build();
  }

  public static List<MatchingRecommendationResponseDto> toDto(List<Manager> managerList) {
    return managerList.stream().map(MatchingRecommendationResponseDto::toDto)
        .collect(Collectors.toList());
  }
}
