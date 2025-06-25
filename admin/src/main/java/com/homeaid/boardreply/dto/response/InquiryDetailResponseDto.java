package com.homeaid.boardreply.dto.response;

import com.homeaid.boardreply.domain.BoardReply;
import com.homeaid.domain.enumerate.UserRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InquiryDetailResponseDto {

  private Long id;
  private Long adminId;
  private Long boardId;
  private String title;
  private String content;
  private UserRole userRole;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String userName;

  public static InquiryDetailResponseDto from(BoardReply reply, String userName) {
    return InquiryDetailResponseDto.builder()
        .id(reply.getId())
        .adminId(reply.getAdminId())
        .boardId(reply.getBoardId())
        .title(reply.getTitle())
        .content(reply.getContent())
        .userRole(reply.getUserRole())
        .createdAt(reply.getCreatedAt())
        .updatedAt(reply.getUpdatedAt())
        .userName(userName)
        .build();
  }
}