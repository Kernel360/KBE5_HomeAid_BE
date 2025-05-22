package com.example.homeaid.adminreply.dto.Request;

import com.example.homeaid.adminreply.entity.AdminReply;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminReplyRequestDto {

  @NotNull(message = "내용을 작성해 주세요")
  private String content;

  public static AdminReply toEntity(AdminReplyRequestDto dto) {
    return AdminReply.builder()
        .content(dto.getContent())
        .build();
  }

}
