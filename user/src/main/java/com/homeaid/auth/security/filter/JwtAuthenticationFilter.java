package com.homeaid.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeaid.auth.service.RefreshTokenService;
import com.homeaid.auth.security.jwt.JwtTokenProvider;
import com.homeaid.auth.user.CustomUserDetails;
import com.homeaid.auth.util.CookieUtil;
import com.homeaid.dto.request.SignInRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private static final String TOKEN_HEADER = "Authorization";
  private static final String TOKEN_PREFIX = "Bearer ";

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;
  private final CookieUtil cookieUtil;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
      JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService,
      CookieUtil cookieUtil) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
    this.refreshTokenService = refreshTokenService;
    this.cookieUtil = cookieUtil;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response)
      throws AuthenticationException {

    try {
      // 토큰 유효성 검증 및 인증 처리
      SignInRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(),
          SignInRequestDto.class);
      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(requestDto.getPhone(), requestDto.getPassword());
      return authenticationManager.authenticate(authToken);
    } catch (IOException e) {
      throw new RuntimeException("인증 요청 파싱 실패", e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {

    CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
    Long userId = userDetails.getUserId();
    String role = userDetails.getAuthorities().iterator().next().getAuthority();
    String username = userDetails.getUser().getName();

    // AT & RT 생성
    String accessToken = jwtTokenProvider.createAccessToken(userId, role);
    String refreshToken = jwtTokenProvider.createRefreshToken(userId);

    // AT를 헤더로 전달
    response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + accessToken);

    // Redis에 RT 저장
    refreshTokenService.saveRefreshToken(userId, refreshToken);

    // RT를 httpOnly 쿠키에 저장
    Cookie refreshCookie = cookieUtil.buildRefreshCookie(refreshToken);
    response.addCookie(refreshCookie);

    // 응답 Body에 필요한 사용자 정보만 포함
    Map<String, Object> responseBody = Map.of(
        "userId", userId,
        "username", username,
        "role", role
    );

    response.setContentType("application/json; charset=UTF-8");
    new ObjectMapper().writeValue(response.getWriter(), responseBody);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed) throws IOException, ServletException {

    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
  }
}
