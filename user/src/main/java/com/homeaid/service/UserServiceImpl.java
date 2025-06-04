package com.homeaid.service;

import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.dto.request.SignInRequestDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.UserRepository;
import com.homeaid.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Manager signUpManager(@Valid Manager manager) {

        if (userRepository.existsByEmail(manager.getEmail())) {
            throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        userRepository.save(manager);
        return manager;
    }

    @Override
    public Customer signUpCustomer(Customer customer) {

        if (userRepository.existsByEmail(customer.getEmail())) {
            throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        userRepository.save(customer);
        return customer;
    }

    public String loginAndGetToken(SignInRequestDto request) {

        var user = userRepository.findByEmail(request.getEmail());

        if (user.isEmpty()) {
            log.warn("Login failed - User not found: email={}", request.getEmail());
            throw new CustomException(UserErrorCode.LOGIN_FAILED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            log.warn("Login failed - Invalid password: email={}", request.getEmail());
            throw new CustomException(UserErrorCode.LOGIN_FAILED);
        }

        return jwtUtil.createJwt(user.get().getId(), user.get().getEmail(), user.get().getRole().name(), 3600000L);
    }

}
