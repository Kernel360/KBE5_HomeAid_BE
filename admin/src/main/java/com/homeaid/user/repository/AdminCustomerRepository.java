package com.homeaid.user.repository;

import com.homeaid.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminCustomerRepository extends JpaRepository<Customer, Long>,
    JpaSpecificationExecutor<Customer> {

  Page<Customer> findByPhoneContaining(String phone, Pageable pageable);
  Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
