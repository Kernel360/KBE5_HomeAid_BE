package com.example.homeaid.admin.service;

import com.example.homeaid.customer.customer.entity.Customer;
import com.example.homeaid.manager.manager.entity.Manager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface AdminService {


    Page<Customer> getCustomersBy(Pageable pageable);

    Page<Manager> getManagersBy(Pageable pageable);


}
