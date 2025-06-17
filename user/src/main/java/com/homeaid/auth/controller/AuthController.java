package com.homeaid.auth.controller;

import com.homeaid.auth.dto.TokenResponse;
import com.homeaid.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessToken) {
    authService.logout(stripBearer(accessToken));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/refresh/reissue")
  public ResponseEntity<TokenResponse> reissue(@CookieValue("refresh_token") String refreshToken) {
    TokenResponse newTokens = authService.reissueToken(refreshToken);
    return ResponseEntity.ok(newTokens);
  }

  private String stripBearer(String tokenHeader) {
    return tokenHeader.replace("Bearer ", "");
  }

}