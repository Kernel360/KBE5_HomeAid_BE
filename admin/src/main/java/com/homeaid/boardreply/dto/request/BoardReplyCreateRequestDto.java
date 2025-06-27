package com.homeaid.boardreply.dto.request;

import com.homeaid.domain.BoardReply;
import com.homeaid.domain.User;
import com.homeaid.domain.enumerate.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardReplyCreateRequestDto {

  @NotNull(message = "내용을 작성해 주세요")
  private String content;

  // userId, userRole은 서비스단에서 세팅
  public static BoardReply toEntity(Long boardId, Long adminId, User user, UserRole userRole, BoardReplyCreateRequestDto dto) {
    return BoardReply.builder()
        .boardId(boardId)
        .adminId(adminId)
        .user(user)
        .userRole(userRole)
        .content(dto.getContent())
        .build();
  }

}
