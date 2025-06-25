package com.homeaid.dto.response;


import com.homeaid.domain.BoardReply;
import com.homeaid.domain.UserBoard;
import com.homeaid.domain.enumerate.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시글 상세 응답 DTO")
public class UserBoardResponseDto {

  @Schema(description = "게시글 ID")
  private Long id;

  @Schema(description = "작성자 ID", example = "1")
  private Long userId;

  @Schema(description = "문의글 제목", example = "예약 관련 문의드립니다.")
  private String title;

  @Schema(description = "게시글 내용", example = "예약 시간 변경 가능한가요?")
  private String content;

  @Schema(description = "작성자 권한", example = "CUSTOMER",
      allowableValues = {"CUSTOMER", "MANAGER"})
  private UserRole role;

  @Schema(description = "답변 등록 여부", example = "1")
  private boolean isAnswered;

  @Schema(description = "게시글 작성일시", example = "2024-12-01T10:30:00")
  private LocalDateTime createdAt;

  private ReplyDto reply;

  @Getter
  @Builder
  @AllArgsConstructor
  public static class ReplyDto {
    private Long replyId;
    private String content;
    private String adminName;
    private LocalDateTime createdAt;
  }

  public static UserBoardResponseDto toDto(UserBoard userBoard) {
    BoardReply reply = userBoard.getReply();
    return UserBoardResponseDto.builder()
        .id(userBoard.getId())
        .userId(userBoard.getUserId())
        .title(userBoard.getTitle())
        .content(userBoard.getContent())
        .role(userBoard.getRole())
        .isAnswered(userBoard.isAnswered())
        .createdAt(userBoard.getCreatedAt())
        .reply(reply != null ? ReplyDto.builder()
            .replyId(reply.getId())
            .content(reply.getContent())
            .adminName(reply.getUser() != null ? reply.getUser().getName() : null)
            .createdAt(reply.getCreatedAt())
            .build() : null)
        .build();
  }
}