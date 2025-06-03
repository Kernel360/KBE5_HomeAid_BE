package com.homeaid.service;

import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.dto.request.SwaggerSignInRequestDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.UserRepository;
import com.homeaid.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public String loginAndGetToken(SwaggerSignInRequestDto request) {
        var user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(UserErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(UserErrorCode.LOGIN_FAILED);
        }

        return jwtUtil.createJwt(user.getId(), user.getEmail(), user.getRole().name(), 1800000L);
    }

}
