package com.example.homeaid.managerboard.dto.request;

import com.example.homeaid.managerboard.entity.ManagerBoard;
import lombok.Getter;

@Getter
public class ManagerBoardUpdateRequestDto {

  private String title;
  private String content;
  private Boolean isPublic;

  // 업데이트용 Entity로 변환 (id는 Controller에서 보관)
  public ManagerBoard toEntity() {
    return ManagerBoard.builder()
        .title(this.title)
        .content(this.content)
        .isPublic(this.isPublic)
        .build();
  }
}
