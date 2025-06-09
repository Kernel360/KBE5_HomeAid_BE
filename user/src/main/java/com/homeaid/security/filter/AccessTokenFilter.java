package com.homeaid.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeaid.domain.User;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.security.user.CustomUserDetails;
import com.homeaid.security.user.CustomUserDetailsService;
import com.homeaid.security.token.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class AccessTokenFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomUserDetailsService customUserDetailsService;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final String TOKEN_HEADER = "Authorization";
  private static final String TOKEN_PREFIX = "Bearer ";

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/api/v1/users/signup")
        || path.startsWith("/api/v1/auth/signin")
        || path.startsWith("/api/v1/auth/refresh");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // 토큰 추출
    String authorization = request.getHeader(TOKEN_HEADER);
    // Authorizatioin 헤더가 없거나 형식이 잘못되었으면 다음 핉터로 넘김
    if (authorization == null || !authorization.startsWith(TOKEN_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    // Bearer 토큰 추출
    String token = authorization.substring(TOKEN_PREFIX.length());

    try {
      // 토큰 만료 여부 확인
      jwtTokenProvider.validateToken(token);

      // 토큰에서 userId과 role 획득
      Long userId = jwtTokenProvider.getUserIdFromToken(token);
      String role = jwtTokenProvider.getRoleFromToken(token);

      // 실제 DB 조회 없이 임시 User 객체 생성
      User user = new User(userId,
          UserRole.valueOf(role.replace("ROLE_", ""))); // ROLE 접두사 제거 필요
      CustomUserDetails customUserDetails = new CustomUserDetails(user);

      // 인증 객체 생성 후 SecurityContext에 등록
      Authentication authentication = new UsernamePasswordAuthenticationToken(
          customUserDetails,
          null,
          customUserDetails.getAuthorities()
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (ExpiredJwtException e) {
      // 토큰 만료 시 에러 응답
      System.out.println("JWT 토큰이 만료되었습니다: " + e.getMessage());
      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT_EXPIRED",
          "토큰이 만료되었습니다.");
      return;
    } catch (JwtException e) {
      // 기타 JWT 관련 에러
      System.out.println("JWT 처리 중 에러 발생: " + e.getMessage());
      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT_INVALID",
          "유효하지 않은 토큰입니다.");
      return;
    } catch (Exception e) {
      // 기타 예외
      System.out.println("인증 처리 중 에러 발생: " + e.getMessage());
      sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "AUTH_ERROR",
          "인증 처리 중 오류가 발생했습니다.");
      return;
    }
    filterChain.doFilter(request, response);
  }

  private void sendErrorResponse(HttpServletResponse response, int status, String errorCode, String message)
      throws IOException {
    response.setStatus(status);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    Map<String, String> errorResponse = Map.of(
        "error", errorCode,
        "message", message
    );
    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}

