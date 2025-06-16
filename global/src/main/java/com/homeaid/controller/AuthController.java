package com.homeaid.controller;

import com.homeaid.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {
  private final AuthService authService;

  @PostMapping("/signin")
  public ResponseEntity<TokenResponse> signin(@RequestBody SigninRequest request) {
    TokenResponse tokens = authService.signIn(request.username(), request.password());
    return ResponseEntity.ok(tokens);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessToken) {
    authService.logout(stripBearer(accessToken));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refresh(@CookieValue("refreshToken") String refreshToken) {
    TokenResponse newTokens = authService.refreshAccessToken(refreshToken);
    return ResponseEntity.ok(newTokens);
  }

  private String stripBearer(String tokenHeader) {
    return tokenHeader.replace("Bearer ", "");
  }
}
