package com.homeaid.auth.security.filter;

import com.homeaid.auth.service.OAuthCodeService;
import com.homeaid.auth.user.CustomOAuth2User;
import com.homeaid.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final OAuthCodeService oauthCodeService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {

    log.info("OAuth2 인증 성공");

    try {
      CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
      User user = oauth2User.getUser();
      log.info("확인: {}", user.isProfileComplete());

      // profileComplete 여부와 무관하게 임시 코드 발급
      String oauthCode = UUID.randomUUID().toString();
      oauthCodeService.store(oauthCode, user.getId());

      // 리다이렉트 URL 구성 - 임시 토큰 직접 전달
      String redirectUrl = "http://localhost:3000/auth/oauth/callback"
          + "?oauthCode=" + oauthCode
          + "&email=" + user.getEmail()
          + "&profileComplete=" + user.isProfileComplete();
      response.sendRedirect(redirectUrl);
      // false -> 추가정보 입력, true -> 임시토큰으로 jwt 토큰 발급 요청

    } catch (Exception e) {
      log.error("OAuth2 인증 성공 처리 중 오류 발생", e);
      response.sendRedirect("/login?error=true");
    }
  }
}