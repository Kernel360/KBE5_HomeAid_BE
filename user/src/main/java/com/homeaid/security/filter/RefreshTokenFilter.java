package com.homeaid.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeaid.security.token.JwtTokenProvider;
import com.homeaid.security.user.CustomUserDetails;
import com.homeaid.security.user.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomUserDetailsService customUserDetailsService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  // refresh 요청만 필터링
  private static final AntPathRequestMatcher REFRESH_REQUEST_MATCHER =
      new AntPathRequestMatcher("/api/v1/user/auth/refresh", "POST");

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !REFRESH_REQUEST_MATCHER.matches(request);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    Optional<Cookie> refreshTokenCookie = getRefreshTokenFromCookies(request);

    if (refreshTokenCookie.isEmpty()) {
      respondWithError(response, HttpServletResponse.SC_UNAUTHORIZED, "RT_MISSING", "리프레시 토큰이 없습니다.");
      return;
    }

    String refreshToken = refreshTokenCookie.get().getValue();

    try {
      if (jwtTokenProvider.isTokenExpired(refreshToken)) {
        respondWithError(response, HttpServletResponse.SC_UNAUTHORIZED, "RT_EXPIRED", "리프레시 토큰이 만료되었습니다.");
        return;
      }

      Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
      // DB 조회를 통해 유저 검증
      CustomUserDetails userDetails =
          (CustomUserDetails) customUserDetailsService.loadUserByUsernameById(userId);

      // 새로운 엑세스 토큰 발급
      String newAccessToken = jwtTokenProvider.createJwt(userId, userDetails.getUserRole().name());

      response.setHeader("Authorization", "Bearer " + newAccessToken);
      response.setContentType("application/json");
      objectMapper.writeValue(response.getWriter(), Map.of("accessToken", newAccessToken));

    } catch (Exception e) {
      respondWithError(response, HttpServletResponse.SC_UNAUTHORIZED, "RT_INVALID", "유효하지 않은 리프레시 토큰입니다.");
    }
  }

  private Optional<Cookie> getRefreshTokenFromCookies(HttpServletRequest request) {
    if (request.getCookies() == null) return Optional.empty();

    for (Cookie cookie : request.getCookies()) {
      if ("refresh_token".equals(cookie.getName())) {
        return Optional.of(cookie);
      }
    }

    return Optional.empty();
  }

  private void respondWithError(HttpServletResponse response, int status, String code, String message)
      throws IOException {
    response.setStatus(status);
    response.setContentType("application/json");
    objectMapper.writeValue(response.getWriter(), Map.of(
        "error", code,
        "message", message
    ));
  }
}
