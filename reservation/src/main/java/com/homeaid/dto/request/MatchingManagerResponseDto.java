package com.homeaid.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MatchingManagerResponseDto {

  @NotNull
  private Action action;

  private String memo;

  public enum Action {
    ACCEPT,
    REJECT
  }
}
