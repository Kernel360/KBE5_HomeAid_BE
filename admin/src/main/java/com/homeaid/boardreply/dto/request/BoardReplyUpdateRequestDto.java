package com.homeaid.boardreply.dto.request;

import com.homeaid.domain.BoardReply;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardReplyUpdateRequestDto {

  @NotNull(message = "내용을 작성해 주세요")
  private String content;

  public static BoardReply toEntity(BoardReplyUpdateRequestDto dto) {
    return BoardReply.builder()
        .content(dto.getContent())
        .build();
  }

}
