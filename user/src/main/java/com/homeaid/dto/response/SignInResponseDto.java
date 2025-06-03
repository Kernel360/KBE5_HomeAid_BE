package com.homeaid.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponseDto {

  private String token;

  public SignInResponseDto(String token) {
    this.token = token;
  }

}
