package com.homeaid.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeaid.dto.request.SignInRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    try {
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
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult
  ) throws IOException, ServletException {

    CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
    Long userId = userDetails.getUserId();
    String email = userDetails.getUsername();
    String role = userDetails.getAuthorities().iterator().next().getAuthority();

    // 토큰 생성
    String token = jwtTokenProvider.createJwt(userId, role); // 유효시간 1시간
    response.addHeader("Authorization", "Bearer " + token);
    new ObjectMapper().writeValue(response.getWriter(), Map.of("token", token));

    // Todo RT 생성 시, RT를 쿠키에 저장
    // httpOnly 쿠키 설정
    Cookie cookie = new Cookie("jwt", token);
    cookie.setHttpOnly(true);
    cookie.setSecure(true); // HTTPS 사용 시에만 true로 설정
    cookie.setPath("/");
    cookie.setMaxAge(3600); // 1시간
    cookie.setAttribute("SameSite", "Strict");

    // 응답에 쿠키 추가
    response.addCookie(cookie);

    // 응답 본문에 필요한 사용자 정보만 포함
    Map<String, Object> responseBody = Map.of(
        "userId", userId,
        "email", email,
        "role", role,
        "message", "로그인 성공"
    );

    response.setContentType("application/json");
    new ObjectMapper().writeValue(response.getWriter(), responseBody);

  }

  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed
  ) throws IOException, ServletException {

    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

  }
}
