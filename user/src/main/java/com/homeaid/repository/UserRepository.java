package com.homeaid.repository;

import com.homeaid.domain.User;
import com.homeaid.domain.enumerate.Weekday;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


  @Query(value = """
    SELECT DISTINCT u.*
    FROM user u
    JOIN manager m ON m.id = u.id
    JOIN manager_availability a ON a.manager_id = m.id
    JOIN manager_service_option s ON s.manager_id = m.id
    JOIN service_sub_option sub ON sub.name = :subOptionName
    WHERE u.user_type = 'MANAGER'
      AND a.weekday = :reservationWeekday
      AND a.start_time <= :startTime
      AND a.end_time >= :endTime
      AND s.service_option_id = sub.service_option_id
    LIMIT 10
    """, nativeQuery = true)
  List<User> findMatchingManagers(Weekday reservationWeekday, LocalTime startTime, LocalTime endTime, String subOptionName);

  boolean existsByEmail(String email);

}
