package com.homeaid.service;

import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.domain.User;
import jakarta.validation.Valid;

public interface UserService {

    Manager signUpManager(@Valid Manager manager);

    Customer signUpCustomer(@Valid Customer customer);

    User getUserById(Long id);

}
