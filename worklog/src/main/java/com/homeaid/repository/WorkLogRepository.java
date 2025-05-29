package com.homeaid.repository;

import com.homeaid.domain.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    Optional<WorkLog> findByReservationId(Long reservationId);
}
