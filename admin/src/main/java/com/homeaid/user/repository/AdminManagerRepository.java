package com.homeaid.user.repository;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminManagerRepository extends JpaRepository<Manager, Long> {

  Page<Manager> findByNameContainingIgnoreCase(String name, Pageable pageable);

  Page<Manager> findByPhoneContaining(String phone, Pageable pageable);

  Page<Manager> findByCareerContaining(String career, Pageable pageable);

  Page<Manager> findByStatus(ManagerStatus status, Pageable pageable);
}
