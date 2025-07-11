package com.homeaid.auth.controller;

import com.homeaid.auth.dto.TokenResponse;
import com.homeaid.auth.dto.request.AdditionalUserInfoDto;
import com.homeaid.auth.dto.request.CustomerSignUpRequestDto;
import com.homeaid.auth.dto.request.ManagerSignUpRequestDto;
import com.homeaid.auth.dto.response.OauthResponseDto;
import com.homeaid.auth.dto.response.SignUpResponseDto;
import com.homeaid.auth.service.AuthService;
import com.homeaid.auth.util.CookieUtil;
import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PatchMapping;
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
  private final UserService userService;

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

  @PostMapping("/signup/customers")
  public ResponseEntity<CommonApiResponse<SignUpResponseDto>> signUpCustomer(
      @RequestBody @Valid CustomerSignUpRequestDto customerSignUpRequestDto
  ) {

    String password = passwordEncoder.encode(customerSignUpRequestDto.getPassword());

    Customer customer = authService.signUpCustomer(
        CustomerSignUpRequestDto.toEntity(customerSignUpRequestDto, password) // 비밀번호 추가 수정 필요
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(SignUpResponseDto.toCustomerDto(customer)));
  }

  @PatchMapping("/oauth-additional-profile")
  public ResponseEntity<CommonApiResponse<Void>> updateAdditionalUserInfo(
      @Valid @RequestBody AdditionalUserInfoDto dto,
      HttpServletResponse response
  ) {
    authService.additionalOAuthInfo(dto);
    return ResponseEntity.ok(CommonApiResponse.success(null));
  }

  @PostMapping("/logout")
  public ResponseEntity<CommonApiResponse<Void>> logout(
      @RequestHeader("Authorization") String accessToken) {
    authService.logout(stripBearer(accessToken));
    return ResponseEntity.ok(CommonApiResponse.success(null));
  }

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

  // oauth 토큰 발급
  @PostMapping("/oauth/token")
  public ResponseEntity<OauthResponseDto> issueTokenFromOAuthCode(
      @RequestBody Map<String, String> request,
      HttpServletResponse response
  ) {
    String oauthCode = request.get("oauthCode");
    OauthResponseDto oauthResponse = authService.issueTokenFromOAuthCode(oauthCode);

    // RT 쿠키 저장
    Cookie refreshCookie = cookieUtil.buildRefreshCookie(oauthResponse.getRefreshToken());
    response.addCookie(refreshCookie);

    // AT 헤더 저장
    response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + oauthResponse.getAccessToken());

    return ResponseEntity.ok(oauthResponse);
  }

  private String stripBearer(String tokenHeader) {
    return tokenHeader.replace("Bearer ", "");
  }

}