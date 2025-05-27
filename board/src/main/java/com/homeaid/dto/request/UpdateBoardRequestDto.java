package com.homeaid.dto.request;


import com.homeaid.domain.CustomerBoard;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateBoardRequestDto {

  @NotBlank(message = "제목을 작성해 주세요")
  private String title;

  @NotBlank(message = "내용을 작성해 주세요")
  private String content;

  public static CustomerBoard toEntity(UpdateBoardRequestDto dto) {
    return CustomerBoard.builder()
        .title(dto.getTitle())
        .content(dto.getContent())
        .build();
  }

}
