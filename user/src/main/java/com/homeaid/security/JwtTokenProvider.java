package com.homeaid.security;

import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final SecretKey secretKey;
  private final Long accessTokenExpirationMs;


  public JwtTokenProvider(
      @Value("${spring.jwt.secret}") String secret,
      @Value ("${spring.jwt.access-token-expire-time}") Long accessTokenExpirationMs
  ) {
    this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
    this.accessTokenExpirationMs = accessTokenExpirationMs;
  }

  public String getEmailFromToken(String token) {
    return Jwts.parser().verifyWith(secretKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .get("email", String.class);
  }

  public String getRoleFromToken(String token) {
    return Jwts.parser().verifyWith(secretKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .get("role", String.class);
  }

  public Long getUserIdFromToken(String token) {
    return Jwts.parser().verifyWith(secretKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .get("userId", Long.class);
  }

  public Boolean isTokenExpired(String token) {
    return Jwts.parser().verifyWith(secretKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration()
        .before(new Date());
  }


  public String createJwt(Long userId, String email, String role) {
    return Jwts.builder()
        .claim("userId", userId)
        .claim("email", email)
        .claim("role", role)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
        .signWith(secretKey)
        .compact();
  }

}
