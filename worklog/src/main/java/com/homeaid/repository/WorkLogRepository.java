package com.homeaid.repository;

import com.homeaid.domain.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    boolean existsWorkLogByManagerId(Long managerId);
}
