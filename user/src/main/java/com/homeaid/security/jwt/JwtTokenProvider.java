package com.homeaid.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  private final SecretKey secretKey;
  private final Long accessTokenExpirationMs;
  private final Long refreshTokenExpirationMs;

  private static final String TOKEN_HEADER = "Authorization";
  private static final String TOKEN_PREFIX = "Bearer ";

  public JwtTokenProvider(
      @Value("${spring.jwt.secret}") String secret,
      @Value("${spring.jwt.access-token-expire-time}") Long accessTokenExpirationMs,
      @Value("${spring.jwt.refresh-token-expire-time}") Long refreshTokenExpirationMs
  ) {
    this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
    this.accessTokenExpirationMs = accessTokenExpirationMs;
    this.refreshTokenExpirationMs = refreshTokenExpirationMs;
  }

  // Access Token 생성
  public String createAccessToken(Long userId, String role) {
    return Jwts.builder()
        .claim("userId", userId)
        .claim("role", role)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
        .signWith(secretKey)
        .compact();
  }

  // Refresh Token 생성
  public String createRefreshToken(Long userId) {
    return Jwts.builder()
        .claim("userId", userId)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
        .compact();
  }

  // 토큰에서 userRole 추출
  public String getRoleFromToken(String token) {
    return Jwts.parser().verifyWith(secretKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .get("role", String.class);
  }

  // 토큰에서 userId 추출
  public Long getUserIdFromToken(String token) {
    return Jwts.parser().verifyWith(secretKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .get("userId", Long.class);
  }

  // 토큰에서 만료기간 추출
  public Boolean isTokenExpired(String token) {
    return Jwts.parser().verifyWith(secretKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration()
        .before(new Date());
  }

  public long getRemainingTime(String token) {
    Date expiration = Jwts.parser().verifyWith(secretKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration();

    return expiration.getTime() - System.currentTimeMillis();
  }

  // 토큰 유효성 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
      return true;
    } catch (SecurityException e) {
      log.error("잘못된 JWT 서명입니다.");
    } catch (MalformedJwtException e) {
      log.error("잘못된 JWT 토큰입니다.");
    } catch (ExpiredJwtException e) {
      log.error("만료된 JWT 토큰입니다.");
    } catch (UnsupportedJwtException e) {
      log.error("지원되지 않는 JWT 토큰입니다.");
    } catch (IllegalArgumentException e) {
      log.error("JWT 토큰이 잘못되었습니다.");
    }
    return false;
  }

  // 토큰 추출
  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(TOKEN_HEADER);
    if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
      return bearerToken.substring(TOKEN_PREFIX.length());
    }
    return null;
  }
}
