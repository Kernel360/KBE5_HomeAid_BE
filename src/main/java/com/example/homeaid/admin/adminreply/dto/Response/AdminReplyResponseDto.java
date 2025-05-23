package com.example.homeaid.admin.adminreply.dto.Response;

import com.example.homeaid.admin.adminreply.entity.AdminReply;
import com.example.homeaid.admin.adminreply.entity.PostType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminReplyResponseDto {

  private Long id;
  private PostType postType;
  private String content;
  private LocalDateTime createdAt;
  private Long postId; // 게시글 ID
  private Long adminId;

  public static AdminReplyResponseDto toDto(AdminReply entity) {
    return AdminReplyResponseDto.builder()
        .id(entity.getId())
        .postType(entity.getPostType())
        .content(entity.getContent())
        .createdAt(entity.getCreatedAt())
        .postId(entity.getPostId())
        .adminId(entity.getAdminId())
        .build();
  }

}
