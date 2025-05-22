package com.example.homeaid.managerboard.dto.response;

import com.example.homeaid.managerboard.entity.ManagerBoard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManagerBoardResponseDto {

  private Long id;
  private String title;
  private String content;
  private Boolean isPublic;
  private String createdAt;
  private Long managerId;

  public static ManagerBoardResponseDto from(ManagerBoard managerBoard) {
    return ManagerBoardResponseDto.builder()
        .id(managerBoard.getId())
        .title(managerBoard.getTitle())
        .content(managerBoard.getContent())
        .isPublic(managerBoard.getIsPublic())
        .createdAt(managerBoard.getCreatedAt() != null ? managerBoard.getCreatedAt().toString() : null)
        .managerId(managerBoard.getManagerId())
        .build();
  }
}