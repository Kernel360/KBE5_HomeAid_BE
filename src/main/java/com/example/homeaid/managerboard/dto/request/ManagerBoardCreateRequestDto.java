package com.example.homeaid.managerboard.dto.request;

import com.example.homeaid.managerboard.entity.ManagerBoard;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ManagerBoardCreateRequestDto {

  private String title;
  private String content;
  private Boolean isPublic;

  public static ManagerBoard toEntity(ManagerBoardCreateRequestDto managerBoardCreateRequestDto) {
    return ManagerBoard.builder()
        .title(managerBoardCreateRequestDto.getTitle())
        .content(managerBoardCreateRequestDto.getContent())
        .isPublic(managerBoardCreateRequestDto.getIsPublic())
        .createdAt(LocalDateTime.now())
        .managerId(1L) // 수정필수
        .build();
  }
}
