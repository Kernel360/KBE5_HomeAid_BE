package com.example.homeaid.managerboard.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateManagerBoardRequestDto {

  @NotBlank(message = "제목을 작성해주세요")
  private String title;

  @NotBlank(message = "내용을 작성해주세요")
  private String content;
  private Boolean isPublic;

}
