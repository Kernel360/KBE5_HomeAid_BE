package com.example.homeaid.admin.service;

import com.example.homeaid.admin.repository.AdminRepository;
import com.example.homeaid.customer.entity.Customer;
import com.example.homeaid.customer.entity.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;


    public Page<Customer> findAllCustomer(Pageable pageable) {

        return customerRepository.findAll(pageable);
    }



}
