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

  // 전체 예약 수 (연/월/일)
  @Query("""
    SELECT COUNT(r) FROM Reservation r
    WHERE YEAR(r.createdDate) = :year
      AND (:month IS NULL OR MONTH(r.createdDate) = :month)
      AND (:day IS NULL OR DAY(r.createdDate) = :day)
  """)
  long countReservations(@Param("year") int year, @Param("month") Integer month, @Param("day") Integer day
  );

  // 취소된 예약 수 (연/월/일)
  @Query("""
    SELECT COUNT(r) FROM Reservation r
    WHERE r.status = 'CANCELLED'
      AND YEAR(r.createdDate) = :year
      AND (:month IS NULL OR MONTH(r.createdDate) = :month)
      AND (:day IS NULL OR DAY(r.createdDate) = :day)
  """)
  long countCancelledReservations(@Param("year") int year, @Param("month") Integer month, @Param("day") Integer day
  );

  // 연도 또는 연도 + 월 기준 평균 처리 시간 (일)
  @Query(value = """
    SELECT AVG(DATEDIFF(DATE(r.modified_date), r.requested_date))
    FROM reservation r
    WHERE YEAR(r.requested_date) = :year
      AND (:month IS NULL OR MONTH(r.requested_date) = :month)
      AND r.status = 'COMPLETED'
  """, nativeQuery = true)
  Double getAverageProcessingDays(@Param("year") int year, @Param("month") Integer month);

  // 완료된 예약 수 (성공률 계산용)
  @Query("""
    SELECT COUNT(r) FROM Reservation r
    WHERE r.status = 'COMPLETED'
      AND YEAR(r.createdDate) = :year
      AND (:month IS NULL OR MONTH(r.createdDate) = :month)
      AND (:day IS NULL OR DAY(r.createdDate) = :day)
  """)
  long countCompletedReservations(@Param("year") int year, @Param("month") Integer month, @Param("day") Integer day
  );

  // 평균 처리 시간 (분) - Native SQL
  @Query(value = """
    SELECT AVG(TIMESTAMPDIFF(MINUTE, r.requested_date, r.modified_date))
    FROM reservation r
    WHERE YEAR(r.requested_date) = :year
      AND (:month IS NULL OR MONTH(r.requested_date) = :month)
      AND (:day IS NULL OR DAY(r.requested_date) = :day)
      AND r.status = 'COMPLETED'
  """, nativeQuery = true)
  Double getAverageProcessingMinutes(@Param("year") int year, @Param("month") Integer month, @Param("day") Integer day
  );

  @Query("SELECT r FROM Reservation r WHERE (:status IS NULL OR r.status = :status)")
  Page<Reservation> findByOptionalStatus(@Param("status") ReservationStatus status, Pageable pageable);

}
