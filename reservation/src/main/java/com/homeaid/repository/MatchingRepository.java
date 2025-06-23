package com.homeaid.repository;

import com.homeaid.domain.Matching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingRepository extends JpaRepository<Matching, Long> {

  @Query("SELECT COUNT(m) FROM Matching m WHERE m.reservation.id = :reservationId")
  int countByReservationId(@Param("reservationId") Long reservationId);

  Page<Matching> findAllByManagerId(Long managerId, Pageable pageable);

  // 성공 매칭 건수
  @Query("""
    SELECT COUNT(m) FROM Matching m
    WHERE m.status = 'CONFIRMED'
      AND YEAR(m.createdDate) = :year
      AND (:month IS NULL OR MONTH(m.createdDate) = :month)
  """)
  long countConfirmedMatchings(@Param("year") int year, @Param("month") Integer month);

  // 실패/취소 매칭 건수
  @Query("""
    SELECT COUNT(m) FROM Matching m
    WHERE m.status IN ('CANCELLED', 'REJECTED')
      AND YEAR(m.createdDate) = :year
      AND (:month IS NULL OR MONTH(m.createdDate) = :month)
  """)
  long countFailedOrCancelledMatchings(@Param("year") int year, @Param("month") Integer month);
}
