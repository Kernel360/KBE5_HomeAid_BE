package com.example.homeaid.admin.adminreply.dto.Request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReplyDto {
  private Long id;
  private String content;
  private LocalDateTime createdAt;
}