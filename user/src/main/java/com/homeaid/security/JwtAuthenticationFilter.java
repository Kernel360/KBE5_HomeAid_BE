package com.homeaid.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeaid.dto.request.SignInRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
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
    String username = userDetails.getUsername();
    String role = userDetails.getAuthorities().iterator().next().getAuthority();

    // AT & RT 생성
    String accessToken = jwtTokenProvider.createJwt(userId, role);
    String refreshToken = jwtTokenProvider.createJwt(userId, role);

    // AT를 헤더로 전달
    response.addHeader("Authorization", "Bearer " + accessToken);

    // RT를 httpOnly 쿠키에 저장
    Cookie rtCookie = new Cookie("refresh_token", refreshToken);
    rtCookie.setHttpOnly(true);
    rtCookie.setSecure(true); // HTTPS 사용 시에만 true로 설정
    rtCookie.setPath("/");
    rtCookie.setMaxAge(7*24*60*60); // 7일
    response.addCookie(rtCookie);
    rtCookie.setAttribute("SameSite", "Strict");

    // 응답 Body에 필요한 사용자 정보만 포함
    Map<String, Object> responseBody = Map.of(
        "userId", userId,
        "username", username,
        "role", role
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
