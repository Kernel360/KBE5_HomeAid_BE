package com.homeaid.auth.service;

import com.homeaid.auth.dto.TokenResponse;
import com.homeaid.auth.dto.request.OAuthSignupRequestDto;
import com.homeaid.auth.dto.request.SignInRequestDto;
import com.homeaid.auth.dto.response.OauthResponseDto;
import com.homeaid.auth.dto.response.SignInResponseDto;
import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.domain.User;
import jakarta.validation.Valid;

public interface AuthService {

  Manager signUpManager(@Valid Manager manager);

  Customer signUpCustomer(@Valid Customer customer);

  void logout(String accessToken);

  TokenResponse reissueToken(String refreshToken);

  String loginAndGetToken(SignInRequestDto request);

  SignInResponseDto issueToken(String oauthCode);

  void completeOAuthSignup(OAuthSignupRequestDto request);

  OauthResponseDto oAuthSignInResponse(User user);
}
