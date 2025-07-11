package com.homeaid.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OauthResponseDto {

  private String accessToken;
  private String refreshToken;
  private Long userId;
  private String username;
  private String role;
}
