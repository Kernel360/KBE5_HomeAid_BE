package com.homeaid.repository;

import com.homeaid.domain.WorkLog;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {

  boolean existsWorkLogByManagerIdAndReservationId(Long managerId, Long reservationId);

  Page<WorkLog> findAllByManagerId(Long userId, Pageable pageable);

  Optional<WorkLog> findByReservationId(Long reservationId);
}
