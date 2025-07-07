package com.homeaid.auth.security.filter;

import com.homeaid.auth.security.jwt.JwtTokenProvider;
import com.homeaid.auth.service.RefreshTokenService;
import com.homeaid.auth.user.CustomOAuth2User;
import com.homeaid.auth.util.CookieUtil;
import com.homeaid.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private static final String TOKEN_HEADER = "Authorization";
  private static final String TOKEN_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;
  private final CookieUtil cookieUtil;


  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authentication) throws IOException, ServletException {
    AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain,
        authentication);
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {

    log.info("OAuth2 인증 성공");

    try {
      CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
      User user = oauth2User.getUser();
      Long userId = oauth2User.getUserId();
      String role = oauth2User.getAuthorities().iterator().next().getAuthority();

      // JWT 토큰 생성
      String accessToken = jwtTokenProvider.createAccessToken(userId, role);
      String refreshToken = jwtTokenProvider.createRefreshToken(userId);

      // 헤더에 Access 토큰 설정
      response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + accessToken);

      // 쿠키에 Refresh 토큰 설정
      refreshTokenService.saveRefreshToken(userId, refreshToken);

      Cookie refreshTokenCookie = cookieUtil.buildRefreshCookie(refreshToken);
      response.addCookie(refreshTokenCookie);

      // 프로필 완성 여부에 따라 리다이렉트
      String redirectUrl = determineRedirectUrl(user);
      response.sendRedirect(redirectUrl);

    } catch (Exception e) {
      log.error("OAuth2 인증 성공 처리 중 오류 발생", e);
      response.sendRedirect("/login?error=true");
    }
  }

  private String determineRedirectUrl(User user) {
    if (user.isProfileComplete()) {
      log.info("사용자 {} 추가 정보 입력 필요", user.getEmail());
      return "/additional-profile"; // 👈 추가 정보 입력 페이지
    }

    // 프로필 완성된 사용자는 역할에 따라 리다이렉트
    return switch (user.getRole()) {
      case MANAGER -> "matching/list";
      case CUSTOMER -> "/customer/service-option";
      default -> "/";
    };
  }
}