package com.homeaid.boardreply.dto.response;

import com.homeaid.boardreply.domain.BoardReply;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardReplyResponseDto {

    private Long id;
    private Long boardId;
    private Long adminId;
    private String content;
    private LocalDateTime createdAt;

    public static BoardReplyResponseDto toDto(BoardReply entity) {
      return BoardReplyResponseDto.builder()
          .id(entity.getId())
          .content(entity.getContent())
          .createdAt(entity.getCreatedAt())
          .boardId(entity.getBoardId())
          .adminId(entity.getAdminId())
          .build();
    }

  }