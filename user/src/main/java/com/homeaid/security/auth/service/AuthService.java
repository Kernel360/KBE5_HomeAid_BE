package com.homeaid.security.auth.service;

import com.homeaid.auth.dto.TokenResponse;
import com.homeaid.auth.exception.TokenErrorCode;
import com.homeaid.auth.service.RefreshTokenService;
import com.homeaid.auth.service.TokenBlacklistService;
import com.homeaid.exception.CustomException;
import com.homeaid.security.jwt.JwtTokenProvider;
import com.homeaid.security.user.CustomUserDetails;
import com.homeaid.security.user.CustomUserDetailsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;
  private final TokenBlacklistService tokenBlacklistService;
  private final CustomUserDetailsService customUserDetailsService;

  public void logout(String accessToken) {
    log.debug("로그아웃 요청: {}", accessToken);
    Long userId = jwtTokenProvider.getUserIdFromToken(accessToken);

    // Redis에서 RefreshToken 삭제
    refreshTokenService.deleteRefreshToken(userId);

    // AccessToken 남은 유효시간 계산
    long expiration = jwtTokenProvider.getRemainingTime(accessToken);

    // AccessToken 블랙리스트에 남은 유효시간 동안 등록
    tokenBlacklistService.blacklistAccessToken(accessToken, expiration);
  }

  public TokenResponse reissueToken(String refreshToken) {
    log.debug("AT 재발급 요청 - RT: {}", refreshToken);

    if (refreshToken == null) {
      throw new CustomException(TokenErrorCode.REFRESH_TOKEN_MISSING);
    }

    if (jwtTokenProvider.isTokenExpired(refreshToken)) {
      throw new CustomException(TokenErrorCode.REFRESH_TOKEN_EXPIRED);
    }

    Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
    CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService
        .loadUserByUsernameById(userId);

    if (!refreshTokenService.isValidRefreshToken(userId, refreshToken)) {
      throw new CustomException(TokenErrorCode.REFRESH_TOKEN_INVALID);
    }

    // 새 토큰 생성
    String newAccessToken = jwtTokenProvider.createAccessToken(userId,
        userDetails.getUserRole().name());
    String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

    // 새 RT 저장
    refreshTokenService.saveRefreshToken(userId, newRefreshToken);
    log.debug("AT/RT 재발급 성공 - AT: {}, RT: {}", newAccessToken, newRefreshToken);

    // 응답 객체 생성
    return new TokenResponse(newAccessToken, newRefreshToken);
  }

}
