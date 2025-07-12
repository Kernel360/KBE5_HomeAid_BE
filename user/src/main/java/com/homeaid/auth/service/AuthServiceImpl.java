package com.homeaid.auth.service;

import com.homeaid.auth.dto.TokenResponse;
import com.homeaid.auth.dto.request.OAuthSignupRequestDto;
import com.homeaid.auth.dto.request.SignInRequestDto;
import com.homeaid.auth.dto.response.OauthResponseDto;
import com.homeaid.auth.dto.response.SignInResponseDto;
import com.homeaid.auth.dto.response.TempOAuthUserInfo;
import com.homeaid.auth.exception.TokenErrorCode;
import com.homeaid.auth.security.jwt.JwtTokenProvider;
import com.homeaid.auth.user.CustomUserDetails;
import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.domain.User;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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
  private final OAuthTempCodeService oauthTempCodeService;

  // 매니저 일반 회원가입
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

  // 고객 일반 회원가입
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

  // 로그아웃
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

  // 토큰 재발급
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

  // 스웨거 로그인
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

  // OAuth 사용자 추가 정보 저장
  @Override
  @Transactional
  public void completeOAuthSignup(OAuthSignupRequestDto request) {

    // 임시 사용자 정보 조회 및 검증
    TempOAuthUserInfo tempUserInfo = validateAndGetTempUserInfo(request.getOauthCode());

    // 중복 가입 확인
    validateDuplicateUser(tempUserInfo);

    // 3. Role에 따른 사용자 생성
    User newUser = createUserByRole(request, tempUserInfo);

    // 4. 사용자 저장
    User savedUser = userRepository.save(newUser);
    log.info("새 OAuth2 사용자 저장 완료: {} ({})", savedUser.getEmail(), savedUser.getRole());

    // 5. 임시 코드 삭제
    oauthTempCodeService.deleteOAuthCode(request.getOauthCode());
  }

  private TempOAuthUserInfo validateAndGetTempUserInfo(String oauthCode) {
    TempOAuthUserInfo tempUserInfo = oauthTempCodeService.getNewUserInfo(oauthCode);

    if (tempUserInfo == null) {
      throw new CustomException(UserErrorCode.TEMP_TOKEN_NOT_FOUND);
    }

    if (tempUserInfo.getExpireTime() < System.currentTimeMillis()) {
      throw new CustomException(UserErrorCode.TOKEN_EXPIRED);
    }

    return tempUserInfo;
  }

  private void validateDuplicateUser(TempOAuthUserInfo tempUserInfo) {
    Optional<User> existingUser = userRepository.findByProviderAndProviderId(
        tempUserInfo.getProvider(),
        tempUserInfo.getProviderId()
    );

    if (existingUser.isPresent()) {
      throw new IllegalArgumentException("이미 가입된 사용자입니다.");
    }
  }

  private User createUserByRole(OAuthSignupRequestDto oAuthSignupRequestDto, TempOAuthUserInfo tempUserInfo) {

    User newUser;
    String randomPassword = RandomStringUtils.randomAlphanumeric(20);

    if (oAuthSignupRequestDto.getRole() == UserRole.MANAGER) {
      newUser = createManagerUser(oAuthSignupRequestDto, tempUserInfo, randomPassword);

    } else if (oAuthSignupRequestDto.getRole() == UserRole.CUSTOMER) {
      newUser = createCustomerUser(oAuthSignupRequestDto, tempUserInfo, randomPassword);

    } else {
      throw new IllegalArgumentException("잘못된 사용자 역할입니다: " + oAuthSignupRequestDto.getRole());
    }

    // 공통 OAuth 정보 설정
    setOAuthInfo(newUser, tempUserInfo);

    return newUser;
  }

  // Manager 사용자 생성
  private Manager createManagerUser(OAuthSignupRequestDto request,
      TempOAuthUserInfo tempUserInfo,
      String randomPassword) {

    return Manager.builder()
        .email(tempUserInfo.getEmail())
        .password(passwordEncoder.encode(randomPassword))
        .name(tempUserInfo.getName())
        .phone(request.getPhone())
        .birth(request.getBirth())
        .gender(request.getGender())
        .career(request.getCareer())
        .experience(request.getExperience())
        .build();
  }

  // Customer 사용자 생성
  private Customer createCustomerUser(OAuthSignupRequestDto request,
      TempOAuthUserInfo tempUserInfo,
      String randomPassword) {

    return Customer.builder()
        .email(tempUserInfo.getEmail())
        .password(passwordEncoder.encode(randomPassword))
        .name(tempUserInfo.getName())
        .phone(request.getPhone())
        .birth(request.getBirth())
        .gender(request.getGender())
        .build();
  }

  // OAuth 정보 설정
  private void setOAuthInfo(User user, TempOAuthUserInfo tempUserInfo) {
    user.setProvider(tempUserInfo.getProvider());
    user.setProviderId(tempUserInfo.getProviderId());
    user.setProfileImageUrl(tempUserInfo.getProfileImageUrl());
  }

  @Override
  public SignInResponseDto issueToken(String oauthCode) {
    log.debug("oauth -> jwt 토큰 발급 요청 - code={}", oauthCode);
    User user = oauthTempCodeService.getExistingUserId(oauthCode);

    String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole().name());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
    refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

    log.debug("[AuthService] AT & RT 생성 완료 - userId={}, accessToken={}, refreshToken={}", user.getId(), accessToken, refreshToken);
    return new SignInResponseDto(accessToken, refreshToken, user);
  }

  // 로그인 완료 시 필요한 유저 정보 반환
  public OauthResponseDto oAuthSignInResponse(User user) {
    return new OauthResponseDto(
        user.getId(),
        user.getName(),
        "ROLE_" + user.getRole().name()
    );
  }
}

