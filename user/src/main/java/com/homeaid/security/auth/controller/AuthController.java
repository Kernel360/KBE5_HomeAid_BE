package com.homeaid.security.auth.controller;

import com.homeaid.auth.dto.TokenResponse;
import com.homeaid.security.auth.service.AuthService;
import com.homeaid.security.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
  private final CookieUtil cookieUtil;

  private static final String TOKEN_HEADER = "Authorization";
  private static final String TOKEN_PREFIX = "Bearer ";

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessToken) {
    authService.logout(stripBearer(accessToken));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/refresh/reissue")
  public ResponseEntity<TokenResponse> reissue(
      @CookieValue("refresh_token") String refreshToken,
      HttpServletResponse response) {

    TokenResponse newTokens = authService.reissueToken(refreshToken);

    // 새로 발급한 AT 헤더에 담기
    response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + newTokens.accessToken());

    // 새로 발급한 RT Cookie에 담기
    Cookie refreshCookie = cookieUtil.buildRefreshCookie(newTokens.refreshToken());
    response.addCookie(refreshCookie);

    return ResponseEntity.ok().build();
  }

  private String stripBearer(String tokenHeader) {
    return tokenHeader.replace("Bearer ", "");
  }

}