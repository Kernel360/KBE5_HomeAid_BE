package com.homeaid.repository;

import com.homeaid.domain.Customer;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

  Collection<Customer> findByIdIn(List<Long> customerIds);
}
