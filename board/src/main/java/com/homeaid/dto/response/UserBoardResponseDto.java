package com.homeaid.dto.response;


import com.homeaid.domain.UserBoard;
import com.homeaid.domain.enumerate.UserRole;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBoardResponseDto {

  private Long id;
  private Long userId;
  private String title;
  private String content;
  private UserRole role;
  private LocalDateTime createdAt;

  public static UserBoardResponseDto toDto(UserBoard userBoard) {
    return UserBoardResponseDto.builder()
        .id(userBoard.getId())
        .userId(userBoard.getUserId())
        .title(userBoard.getTitle())
        .content(userBoard.getContent())
        .role(userBoard.getRole())
        .createdAt(userBoard.getCreatedAt())
        .build();
  }

}
