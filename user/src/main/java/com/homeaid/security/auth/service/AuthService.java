package com.homeaid.security.auth.service;

import com.homeaid.auth.dto.TokenResponse;
import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.dto.request.SignInRequestDto;
import jakarta.validation.Valid;

public interface AuthService {

  Manager signUpManager(@Valid Manager manager);

  Customer signUpCustomer(@Valid Customer customer);

  void logout(String accessToken);

  TokenResponse reissueToken(String refreshToken);

  String loginAndGetToken(SignInRequestDto request);
}
