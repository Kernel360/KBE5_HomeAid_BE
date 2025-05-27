package com.homeaid.dto.response;

import com.homeaid.domain.ManagerBoard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateManagerBoardResponseDto {

  private Long id;
  private String title;
  private String content;

  public static UpdateManagerBoardResponseDto toDto(ManagerBoard managerBoard) {
    return UpdateManagerBoardResponseDto.builder()
        .id(managerBoard.getId())
        .title(managerBoard.getTitle())
        .content(managerBoard.getContent())
        .build();
  }

}
