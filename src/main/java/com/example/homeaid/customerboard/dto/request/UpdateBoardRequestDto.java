package com.example.homeaid.customerboard.dto.request;

import com.example.homeaid.customerboard.entity.CustomerBoard;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateBoardRequestDto {

  @NotBlank(message = "제목을 작성해 주세요")
  private String title;

  @NotNull(message = "내용을 작성해 주세요")
  private String content;

  public CustomerBoard toEntity(UpdateBoardRequestDto updateBoardRequestDto) {
    return CustomerBoard.builder()
        .title(updateBoardRequestDto.getTitle())
        .content(updateBoardRequestDto.getContent())
        .build();
  }

}
