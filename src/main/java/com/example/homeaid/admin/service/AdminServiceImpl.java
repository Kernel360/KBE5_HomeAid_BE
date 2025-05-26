package com.example.homeaid.admin.service;

import com.example.homeaid.customer.customer.entity.Customer;
import com.example.homeaid.customer.repository.CustomerRepository;
import com.example.homeaid.manager.manager.entity.Manager;
import com.example.homeaid.manager.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {
    private final CustomerRepository customerRepository;
    private final ManagerRepository managerRepository;

    @Override
    public Page<Customer> getCustomersBy(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public Page<Manager> getManagersBy(Pageable pageable) {
        return managerRepository.findAll(pageable);
    }
}
