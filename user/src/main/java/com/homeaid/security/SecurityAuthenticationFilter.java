package com.homeaid.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeaid.dto.request.SignInRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
public class SecurityAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    try {
      SignInRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(),
          SignInRequestDto.class);

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());

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
    String token = jwtUtil.createJwt(userId, email, role, 36000000L); // 유효시간 1시간
    response.addHeader("Authorization", "Bearer " + token);
    new ObjectMapper().writeValue(response.getWriter(), Map.of("token", token));
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
