package com.example.homeaid.admin.service;

import com.example.homeaid.global.domain.Entity.User;
import com.example.homeaid.global.domain.Entity.User.Role;
import com.example.homeaid.global.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;


    public Page<User> findAllUser(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }


}
