package com.example.homeaid.manager.managerboard.dto.request;

import com.example.homeaid.admin.adminreply.dto.Request.AdminReplyDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ManagerBoardWithRepliesDto {
  private Long id;
  private String title;
  private String content;
  private AdminReplyDto reply;
}
