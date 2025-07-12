package com.homeaid.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OauthResponseDto {

  private Long userId;
  private String username;
  private String role;
}
