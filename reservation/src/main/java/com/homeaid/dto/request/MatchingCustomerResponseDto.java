package com.homeaid.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MatchingCustomerResponseDto {

  @NotNull
  private CustomerAction action;

  private String memo;

  public enum CustomerAction {
    CONFIRM,
    REJECT
  }

}
