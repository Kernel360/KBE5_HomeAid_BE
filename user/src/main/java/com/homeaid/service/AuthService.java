package com.example.homeaid.global.auth.service;

import com.example.homeaid.global.auth.dto.request.CustomerSignUpRequestDto;
import com.example.homeaid.global.auth.dto.request.ManagerSignUpRequestDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

public interface AuthService {

    void registerManager(@Valid ManagerSignUpRequestDto managerSignUpRequestDto);

    void registerCustomer(@Valid CustomerSignUpRequestDto customerSignUpRequestDto);

}
