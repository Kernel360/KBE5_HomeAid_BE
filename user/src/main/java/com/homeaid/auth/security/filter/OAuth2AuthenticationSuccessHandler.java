package com.homeaid.auth.security.filter;

import com.homeaid.auth.dto.response.TempOAuthUserInfo;
import com.homeaid.auth.service.OAuthTempCodeService;
import com.homeaid.auth.user.CustomOAuth2User;
import com.homeaid.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  @Value("${FRONTEND_REDIRECT_URI}")
  private String frontendRedirectUri;

  private final OAuthTempCodeService oauthTempCodeService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {

    log.info("OAuth2 인증 성공");

    try {
      CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
      User user = oauth2User.getUser();

      String oauthCode = UUID.randomUUID().toString();

      if (user.getId() == null) {
        // 신규 사용자 - Redis에 사용자 임시 정보 저장
        TempOAuthUserInfo tempUserInfo = TempOAuthUserInfo.create(
            user.getProvider(),
            user.getProviderId(),
            user.getEmail(),
            user.getName(),
            user.getProfileImageUrl(),
            oauthCode
        );

        oauthTempCodeService.storeNewUserInfo(oauthCode, tempUserInfo);
        String encodedName = URLEncoder.encode(user.getName(), StandardCharsets.UTF_8);
        String redirectUrl = frontendRedirectUri
            + "?oauthCode=" + oauthCode
            + "&email=" + user.getEmail()
            + "&name=" + encodedName
            + "&profileComplete=false";

        response.sendRedirect(redirectUrl);

      } else {
        // 기존 사용자 - RedisUtil을 활용한 사용자 ID 저장
        oauthTempCodeService.storeExistingUserCode(oauthCode, user.getId());

        String redirectUrl = frontendRedirectUri
            + "?oauthCode=" + oauthCode
            + "&profileComplete=" + user.isProfileComplete();

        response.sendRedirect(redirectUrl);
      }

    } catch (Exception e) {
      log.error("OAuth2 인증 성공 처리 중 오류 발생", e);
      response.sendRedirect("/login?error=true");
    }
  }
}