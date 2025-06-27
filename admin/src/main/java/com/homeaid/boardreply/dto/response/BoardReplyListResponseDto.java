package com.homeaid.boardreply.dto.response;

import com.homeaid.domain.BoardReply;
import com.homeaid.domain.enumerate.UserRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardReplyListResponseDto {

  private Long id;
  private Long boardId;
  private Long adminId;
  private Long userId;
  private String userName;
  private UserRole userRole;
  private String title;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static BoardReplyListResponseDto toDto(BoardReply entity, String userName) {
    return BoardReplyListResponseDto.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .content(entity.getContent())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .boardId(entity.getBoardId())
        .adminId(entity.getAdminId())
        .userId(entity.getUser() != null ? entity.getUser().getId() : null)
        .userName(userName)
        .userRole(entity.getUserRole())
        .build();
  }
}