package com.example.homeaid.managerboard.dto.request;

import com.example.homeaid.managerboard.entity.ManagerBoard;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ManagerBoardCreateRequestDto {

  private String title;
  private String content;
  private Boolean isPublic;

  public ManagerBoard toEntity(Long managerId) {
    return ManagerBoard.builder()
        .title(this.title)
        .content(this.content)
        .isPublic(this.isPublic)
        .createdAt(LocalDateTime.now())
        .managerId(managerId)
        .build();
  }
}
