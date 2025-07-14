package com.homeaid.auth.dto.response;

import com.homeaid.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInResponseDto {

  private String accessToken;
  private String refreshToken;
  private User user;

  public SignInResponseDto(String accessToken) {
    this.accessToken = accessToken;
  }
  public SignInResponseDto(String accessToken, String refreshToken, User user) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.user = user;
  }
}
