package com.homeaid.auth.controller;

import com.homeaid.auth.dto.TokenResponse;
import com.homeaid.auth.dto.request.CustomerSignUpRequestDto;
import com.homeaid.auth.dto.request.ManagerSignUpRequestDto;
import com.homeaid.auth.dto.request.OAuthSignupRequestDto;
import com.homeaid.auth.dto.response.OauthResponseDto;
import com.homeaid.auth.dto.response.SignInResponseDto;
import com.homeaid.auth.dto.response.SignUpResponseDto;
import com.homeaid.auth.service.AuthService;
import com.homeaid.auth.util.CookieUtil;
import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;
  private final BCryptPasswordEncoder passwordEncoder;
  private final CookieUtil cookieUtil;

  private static final String TOKEN_HEADER = "Authorization";
  private static final String TOKEN_PREFIX = "Bearer ";

  // 매니저 회원가입
  @PostMapping("/signup/managers")
  public ResponseEntity<CommonApiResponse<SignUpResponseDto>> signUpManager(
      @RequestBody @Valid ManagerSignUpRequestDto managerSignUpRequestDto
  ) {

    String password = passwordEncoder.encode(managerSignUpRequestDto.getPassword());
    Manager manager = authService.signUpManager(
        ManagerSignUpRequestDto.toEntity(managerSignUpRequestDto, password) // 비밀번호 추가 수정 필요
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(SignUpResponseDto.toManagerDto(manager)));
  }

  // 고객 회원가입
  @PostMapping("/signup/customers")
  public ResponseEntity<CommonApiResponse<SignUpResponseDto>> signUpCustomer(
      @RequestBody @Valid CustomerSignUpRequestDto customerSignUpRequestDto
  ) {

    String password = passwordEncoder.encode(customerSignUpRequestDto.getPassword());

    Customer customer = authService.signUpCustomer(
        CustomerSignUpRequestDto.toEntity(customerSignUpRequestDto, password)
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(SignUpResponseDto.toCustomerDto(customer)));
  }

  // oauth 회원가입
  @PostMapping("/signup/oauth/additional-profile")
  public ResponseEntity<CommonApiResponse<String>> completeOAuthSignup(
      @Valid @RequestBody OAuthSignupRequestDto request
  ) {
    String signInToken = authService.oAuthSignup(request);
    return ResponseEntity.ok(CommonApiResponse.success(signInToken));
  }

  // oauth 토큰 발급
  @PostMapping("/oauth/signin")
  public ResponseEntity<OauthResponseDto> oAuthSignIn(
      @RequestBody Map<String, String> request,
      HttpServletResponse response
  ) {
    String oauthCode = request.get("oauthCode");
    SignInResponseDto signInResponseDto = authService.issueToken(oauthCode);

    // RT 쿠키 저장
    Cookie refreshCookie = cookieUtil.buildRefreshCookie(signInResponseDto.getRefreshToken());
    response.addCookie(refreshCookie);

    // AT 헤더 저장
    response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + signInResponseDto.getAccessToken());

    OauthResponseDto oAuthResponseDto = authService.oAuthSignInResponse(signInResponseDto.getUser());

    return ResponseEntity.ok(oAuthResponseDto);
  }

  // 로그아웃
  @PostMapping("/logout")
  public ResponseEntity<CommonApiResponse<Void>> logout(
      @RequestHeader("Authorization") String accessToken) {
    authService.logout(stripBearer(accessToken));
    return ResponseEntity.ok(CommonApiResponse.success(null));
  }

  // 토큰 만료 시, Refresh 토큰 기반 새 AccessToken 발급
  @PostMapping("/refresh/reissue")
  public ResponseEntity<TokenResponse> reissue(
      @CookieValue("refresh_token") String refreshToken,
      HttpServletResponse response) {

    TokenResponse newTokens = authService.reissueToken(refreshToken);

    // 새로 발급한 AT 헤더에 담기
    response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + newTokens.accessToken());

    // 새로 발급한 RT Cookie에 담기
    Cookie refreshCookie = cookieUtil.buildRefreshCookie(newTokens.refreshToken());
    response.addCookie(refreshCookie);

    return ResponseEntity.ok().build();
  }

  private String stripBearer(String tokenHeader) {
    return tokenHeader.replace("Bearer ", "");
  }
}