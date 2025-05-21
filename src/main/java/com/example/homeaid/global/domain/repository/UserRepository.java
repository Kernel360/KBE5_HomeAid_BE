package com.example.homeaid.global.domain.repository;

import com.example.homeaid.global.domain.Entity.User;
import com.example.homeaid.global.domain.Entity.User.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByRole(Role role, Pageable pageable);

}
