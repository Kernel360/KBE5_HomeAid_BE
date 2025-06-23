package com.homeaid.boardreply.dto.request;

import com.homeaid.boardreply.domain.BoardReply;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardReplyCreateRequestDto {

  @NotNull(message = "내용을 작성해 주세요")
  private String content;

  public static BoardReply toEntity(Long boardId, Long adminId, BoardReplyCreateRequestDto dto) {
    return BoardReply.builder()
        .boardId(boardId)
        .adminId(adminId)
        .content(dto.getContent())
        .build();
  }

}
