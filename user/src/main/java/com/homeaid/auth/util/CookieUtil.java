package com.homeaid.auth.util;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

  @Value("${spring.jwt.cookie-max-age}")
  private int COOKIE_MAX_AGE;

  private static final String REFRESH_TOKEN = "refresh_token";

  public Cookie buildRefreshCookie(String refreshToken) {
    Cookie cookie = new Cookie(REFRESH_TOKEN, refreshToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(false); // HTTPS 사용 시에만 true로 설정
    cookie.setPath("/");
    cookie.setMaxAge(COOKIE_MAX_AGE); // 7일
    return cookie;
  }
}