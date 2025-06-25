package com.homeaid.dto.response;

import com.homeaid.domain.UserBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "게시글 목록 응답 DTO")
public class UserBoardListResponseDto {

  @Schema(description = "게시글 ID", example = "1")
  private Long id;

  @Schema(description = "작성자 ID", example = "123")
  private Long userId;

  private String userName;

  @Schema(description = "게시글 제목", example = "환불 관련 문의드립니다.")
  private String title;

  private String content;

  @Schema(description = "게시글 작성일시", example = "2024-12-01T10:30:00")
  private LocalDateTime createdAt;

  private boolean isAnswered; // 답변 등록 유무

  public static UserBoardListResponseDto toDto(UserBoard userBoard) {
    return UserBoardListResponseDto.builder()
        .id(userBoard.getId())
        .userId(userBoard.getUserId())
        .title(userBoard.getTitle())
        .createdAt(userBoard.getCreatedAt())
        .isAnswered(userBoard.isAnswered())
        .build();
  }

}
