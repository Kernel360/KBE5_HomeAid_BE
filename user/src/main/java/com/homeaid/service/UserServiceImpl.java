package com.homeaid.service;

import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.domain.User;
import com.homeaid.dto.request.SignInRequestDto;
import com.homeaid.dto.request.UserUpdateRequestDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.UserRepository;
import com.homeaid.security.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  public Manager signUpManager(@Valid Manager manager) {

    if (userRepository.existsByPhone(manager.getPhone())) {
      throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
    }

    userRepository.save(manager);
    return manager;
  }

  @Override
  public Customer signUpCustomer(Customer customer) {

    if (userRepository.existsByPhone(customer.getPhone())) {
      throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
    }

    userRepository.save(customer);
    return customer;
  }


  @Override
  public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }

  public String loginAndGetToken(SignInRequestDto request) {
    var user = userRepository.findByPhone(request.getPhone());
    if (user.isEmpty()) {
      log.warn("Login failed - User not found: email={}", request.getPhone());
      throw new CustomException(UserErrorCode.LOGIN_FAILED);
    }

    if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
      log.warn("Login failed - Invalid password: email={}", request.getPhone());
      throw new CustomException(UserErrorCode.LOGIN_FAILED);
    }
    return jwtTokenProvider.createAccessToken(user.get().getId(), user.get().getRole().name());
  }

  @Transactional
  public void updateUserInfo(Long userId, UserUpdateRequestDto dto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    user.updateInfo(dto.getName(), dto.getEmail(), dto.getPhone());
  }
}
