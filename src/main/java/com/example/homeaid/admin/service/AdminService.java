package com.example.homeaid.admin.service;

import com.example.homeaid.customer.entity.Customer;
import com.example.homeaid.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final CustomerRepository customerRepository;


    public Page<Customer> getCustomersBy(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }


}
