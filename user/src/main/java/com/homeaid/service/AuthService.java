package com.homeaid.service;

import com.homeaid.dto.request.CustomerSignUpRequestDto;
import com.homeaid.dto.request.ManagerSignUpRequestDto;
import jakarta.validation.Valid;

public interface AuthService {

    void registerManager(@Valid ManagerSignUpRequestDto managerSignUpRequestDto);

    void registerCustomer(@Valid CustomerSignUpRequestDto customerSignUpRequestDto);

}
