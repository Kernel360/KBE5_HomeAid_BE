package com.example.homeaid.global.auth.service;

import com.example.homeaid.customer.customer.entity.Customer;
import com.example.homeaid.customer.customer.entity.CustomerAddress;
import com.example.homeaid.global.auth.dto.request.CustomerSignUpRequestDto;
import com.example.homeaid.global.auth.dto.request.ManagerSignUpRequestDto;
import com.example.homeaid.global.auth.repository.UserRepository;
import com.example.homeaid.manager.manager.entity.Manager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void registerManager(@Valid ManagerSignUpRequestDto managerDto) {

        if (userRepository.existsByEmail(managerDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다."); // UserException 추가 필요
        }

        Manager manager = Manager.builder()
            .email(managerDto.getEmail())
            .password(passwordEncoder.encode(managerDto.getPassword()))
            .name(managerDto.getName())
            .phone(managerDto.getPhone())
            .birth(managerDto.getBirth())
            .gender(managerDto.getGender())
            .career(managerDto.getCareer())
            .experience(managerDto.getExperience())
            .build();

        userRepository.save(manager);
    }

    @Override
    public void registerCustomer(CustomerSignUpRequestDto customerDto) {
        
        if (userRepository.existsByEmail(customerDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다."); // UserException 추가 필요
        }

        CustomerAddress customerAddress = CustomerAddress.builder()
            .address(customerDto.getAddress())
            .addressDetail(customerDto.getAddressDetail())
            .build();

        Customer customer = Customer.addSingleAddress()
            .email(customerDto.getEmail())
            .password(passwordEncoder.encode(customerDto.getPassword()))
            .name(customerDto.getName())
            .phone(customerDto.getPhone())
            .birth(customerDto.getBirth())
            .gender(customerDto.getGender())
            .address(customerAddress)
            .build();
        
        userRepository.save(customer);
    }

}
