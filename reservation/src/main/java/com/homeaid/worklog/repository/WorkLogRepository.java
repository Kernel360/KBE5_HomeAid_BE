package com.homeaid.worklog.repository;

import com.homeaid.worklog.domain.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {

}
