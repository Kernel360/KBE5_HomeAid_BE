package com.homeaid.service;

import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder passwordEncoder;

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

}
