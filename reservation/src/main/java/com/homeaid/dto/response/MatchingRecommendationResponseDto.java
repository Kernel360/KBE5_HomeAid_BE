package com.homeaid.dto.response;

import com.homeaid.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchingRecommendationResponseDto {

  private Long managerId;

  private String managerName;

  public static MatchingRecommendationResponseDto toDto(User manager) {
    return MatchingRecommendationResponseDto.builder().managerId(manager.getId())
        .managerName(manager.getName()).build();
  }

  public static List<MatchingRecommendationResponseDto> toDto(List<User> managerList) {
    return managerList.stream().map(MatchingRecommendationResponseDto::toDto)
        .collect(Collectors.toList());
  }
}
