package com.homeaid.service;

import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import jakarta.validation.Valid;

public interface UserService {

    Manager signUpManager(@Valid Manager manager);

    Customer signUpCustomer(@Valid Customer customer);

}
