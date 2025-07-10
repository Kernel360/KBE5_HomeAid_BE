package com.homeaid.user.repository;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminManagerRepository extends JpaRepository<Manager, Long>,
    JpaSpecificationExecutor<Manager> {
}

