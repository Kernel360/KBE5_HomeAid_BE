package com.homeaid.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeaid.auth.service.TokenBlacklistService;
import com.homeaid.domain.User;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.security.jwt.JwtTokenProvider;
import com.homeaid.security.user.CustomUserDetails;
import com.homeaid.security.user.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
@Slf4j
@Component
@RequiredArgsConstructor
public class AccessTokenFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
//  private final CustomUserDetailsService customUserDetailsService;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final String TOKEN_PREFIX = "Bearer ";
  private final TokenBlacklistService tokenBlacklistService;

  // 토큰 검증 필터가 불필요한 api 요청 설정
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/api/v1/users/signup")
        || path.startsWith("/api/v1/auth/signin")
        || path.startsWith("/api/v1/auth/refresh/reissue");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // 토큰 추출
    String accessToken = jwtTokenProvider.resolveToken(request);

    // Authorization 헤더가 없거나 형식이 잘못되었으면 다음 필터로 넘김
    if (accessToken == null) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      // 블랙리스트 체크
      if (tokenBlacklistService.isBlacklisted(accessToken)) {
        log.warn("블랙리스트 토큰 사용 시도: " + accessToken);
        sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_BLACKLIST",
            "이미 로그아웃된 토큰입니다.");
        return;
      }

      // 토큰 만료 여부 확인
      jwtTokenProvider.validateToken(accessToken);

      // 토큰에서 userId과 role 획득
      Long userId = jwtTokenProvider.getUserIdFromToken(accessToken);
      String role = jwtTokenProvider.getRoleFromToken(accessToken);

      // 실제 DB 조회 없이 임시 User 객체 생성
      User user = new User(userId, UserRole.valueOf(role.replace("ROLE_", ""))); // ROLE 접두사 제거 필요
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

  private void sendErrorResponse(HttpServletResponse response, int status, String errorCode,
      String message)
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

