package com.example.homeaid.managerboard.dto.request;

import lombok.Getter;

@Getter
public class ManagerBoardUpdateRequestDto {

  private String title;
  private String content;
  private Boolean isPublic;

}
