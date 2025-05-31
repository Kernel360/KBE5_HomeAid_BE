package com.homeaid.dto.response;

import com.homeaid.domain.UserBoard;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserBoardListResponseDto {

  private Long id;
  private Long userId;
  private String title;
  private LocalDateTime createdAt;

  public static UserBoardListResponseDto toDto(UserBoard userBoard) {
    return UserBoardListResponseDto.builder()
        .id(userBoard.getId())
        .userId(userBoard.getUserId())
        .title(userBoard.getTitle())
        .createdAt(userBoard.getCreatedAt())
        .build();
  }

}
