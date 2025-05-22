package com.example.homeaid.manager.managerboard.dto.response;

import com.example.homeaid.manager.managerboard.entity.ManagerBoard;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateManagerBoardResponseDto {

  private Long id;
  private String title;
  private String content;
  private Boolean isPublic;
  private LocalDateTime createdAt;
  private Long managerId;

  public static CreateManagerBoardResponseDto toDto(ManagerBoard managerBoard) {
    return CreateManagerBoardResponseDto.builder()
        .id(managerBoard.getId())
        .title(managerBoard.getTitle())
        .content(managerBoard.getContent())
        .isPublic(managerBoard.getIsPublic())
        .createdAt(managerBoard.getCreatedAt())
        .managerId(managerBoard.getManagerId())
        .build();
  }
}