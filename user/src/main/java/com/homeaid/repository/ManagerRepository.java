package com.homeaid.repository;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.Weekday;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

  @Query(value = """
    SELECT DISTINCT m.*, u.*
    FROM manager m
    JOIN user u ON m.id = u.id
    JOIN manager_availability a ON a.manager_id = m.id
    JOIN manager_service_option s ON s.manager_id = m.id
    JOIN service_sub_option sub ON sub.name = :subOptionName
    WHERE a.weekday = :reservationWeekday
      AND a.start_time <= :startTime
      AND a.end_time >= :endTime
      AND s.service_option_id = sub.option_id
    LIMIT 10
    """, nativeQuery = true)
  List<Manager> findMatchingManagers(
      @Param("reservationWeekday") Weekday reservationWeekday,
      @Param("startTime") LocalTime startTime,
      @Param("endTime") LocalTime endTime,
      @Param("subOptionName") String subOptionName
  );

  @Query("SELECT COUNT(m) FROM Manager m WHERE m.verified = true")
  long countActiveManagers();

  @Query("SELECT COUNT(m) FROM Manager m WHERE m.verified = false")
  long countPendingManagers();

  Collection<Manager> findByIdIn(List<Long> managerIds);
}
