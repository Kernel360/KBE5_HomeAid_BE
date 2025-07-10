package com.homeaid.auth.service;

import com.homeaid.auth.dto.TokenResponse;
import com.homeaid.auth.dto.request.AdditionalUserInfoDto;
import com.homeaid.auth.dto.response.OauthResponseDto;
import com.homeaid.auth.exception.TokenErrorCode;
import com.homeaid.auth.security.jwt.JwtTokenProvider;
import com.homeaid.auth.user.CustomUserDetails;
import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.domain.User;
import com.homeaid.dto.request.SignInRequestDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;
  private final TokenBlacklistService tokenBlacklistService;
  private final CustomUserDetailsService customUserDetailsService;
  private final BCryptPasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final OAuthCodeService oauthCodeService;

  // 매니저 회원가입
  @Override
  @Transactional
  public Manager signUpManager(@Valid Manager manager) {

    if (userRepository.existsByPhone(manager.getPhone())) {
      log.warn("[회원가입 실패] 이미 존재하는 전화번호 - phone={}", manager.getPhone());
      throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
    }

    userRepository.save(manager);
    log.info("[매니저 회원가입 완료] id={}, phone={}", manager.getId(), manager.getPhone());
    return manager;
  }

  // 고객 회원가입
  @Override
  @Transactional
  public Customer signUpCustomer(Customer customer) {

    if (userRepository.existsByPhone(customer.getPhone())) {
      log.warn("[회원가입 실패] 이미 존재하는 전화번호 - phone={}", customer.getPhone());
      throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
    }

    userRepository.save(customer);
    log.info("[고객 회원가입 완료] id={}, phone={}", customer.getId(), customer.getPhone());
    return customer;
  }

  // OAuth 사용자 추가 정보 저장
  @Transactional
  public void additionalOAuthInfo(AdditionalUserInfoDto dto) {

    log.info("사용자 추가 정보 입력 요청: {}", dto.getEmail());
    // 유저 조회
    User user = userRepository.findByEmail(dto.getEmail())
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    // 추가 정보 저장
    user.additionalOAuthInfo(dto.getRole(), dto.getPhone(), dto.getBirth(), dto.getGender());
    userRepository.save(user);
  }

  @Override
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

  @Override
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


  // 스웨거 로그인 관련 메서드
  @Override
  public String loginAndGetToken(SignInRequestDto request) {
    var user = userRepository.findByPhone(request.getPhone());
    if (user.isEmpty()) {
      log.warn("로그인 실패 - User not found: email={}", request.getPhone());
      throw new CustomException(UserErrorCode.LOGIN_FAILED);
    }

    if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
      log.warn("로그인 실패 - Invalid password: email={}", request.getPhone());
      throw new CustomException(UserErrorCode.LOGIN_FAILED);
    }
    return jwtTokenProvider.createAccessToken(user.get().getId(), user.get().getRole().name());
  }

  @Override
  public OauthResponseDto issueTokenFromOAuthCode(String oauthCode) {
    log.debug("임시토큰 -> jwt 토큰 발급 요청 - code={}", oauthCode);
    User user = oauthCodeService.resolve(oauthCode)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole().name());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
    refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

    log.debug("[AuthService] 토큰 생성 완료 - userId={}, accessToken={}, refreshToken={}", user.getId(), accessToken, refreshToken);

    return new OauthResponseDto(
        accessToken,
        refreshToken,
        user.getId(),
        user.getName(),
        "ROLE_" + user.getRole().name()
    );
  }
}

