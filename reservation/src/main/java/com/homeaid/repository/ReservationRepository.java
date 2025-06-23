package com.homeaid.repository;

import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  Page<Reservation> findAllByCustomerId(Long userId, Pageable pageable);

  Page<Reservation> findAllByManagerId(Long managerId, Pageable pageable);

  Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);

  @Query("SELECT COUNT(r) FROM Reservation r WHERE DATE(r.createdDate) = CURRENT_DATE")
  long countTodayReservations();

  // 연도 또는 연도 + 월 기준 예약 수 조회
  @Query("""
    SELECT COUNT(r) FROM Reservation r
    WHERE YEAR(r.createdDate) = :year
      AND (:month IS NULL OR MONTH(r.createdDate) = :month)
    """)
  long countReservationsByYearAndOptionalMonth(@Param("year") int year, @Param("month") Integer month);

  // 연도 또는 연도 + 월 기준 평균 처리 시간 (일)
  @Query(value = """
    SELECT AVG(DATEDIFF(DATE(r.modified_date), r.requested_date))
    FROM reservation r
    WHERE YEAR(r.requested_date) = :year
      AND (:month IS NULL OR MONTH(r.requested_date) = :month)
      AND r.status = 'COMPLETED'
  """, nativeQuery = true)
  Double getAverageProcessingDays(@Param("year") int year, @Param("month") Integer month);

//  @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, r.createdAt, r.completedAt)) " +
//      "FROM Reservation r WHERE YEAR(r.createdAt) = :year AND r.completedAt IS NOT NULL")
//  Double getAverageProcessingTime(@Param("year") int year);
}
