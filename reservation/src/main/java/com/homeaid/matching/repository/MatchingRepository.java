package com.homeaid.matching.repository;

import com.homeaid.matching.controller.enumerate.MatchingStatus;
import com.homeaid.matching.domain.Matching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingRepository extends JpaRepository<Matching, Long> {

  Page<Matching> findAllByManagerId(Long managerId, Pageable pageable);

  // 성공 매칭 건수 (연/월/일)
  @Query("""
    SELECT COUNT(m) FROM Matching m
    WHERE m.status = 'CONFIRMED'
      AND YEAR(m.createdDate) = :year
      AND (:month IS NULL OR MONTH(m.createdDate) = :month)
      AND (:day IS NULL OR DAY(m.createdDate) = :day)
  """)
  long countConfirmedMatchings(@Param("year") int year, @Param("month") Integer month, @Param("day") Integer day
  );

  // 실패/취소 매칭 건수 (연/월/일)
  @Query("""
    SELECT COUNT(m) FROM Matching m
    WHERE m.status IN ('CANCELLED', 'REJECTED')
      AND YEAR(m.createdDate) = :year
      AND (:month IS NULL OR MONTH(m.createdDate) = :month)
      AND (:day IS NULL OR DAY(m.createdDate) = :day)
  """)
  long countFailedOrCancelledMatchings(@Param("year") int year, @Param("month") Integer month, @Param("day") Integer day
  );

  // 전체 매칭 시도 수 (연/월/일)
  @Query("""
    SELECT COUNT(m) FROM Matching m
    WHERE YEAR(m.createdDate) = :year
      AND (:month IS NULL OR MONTH(m.createdDate) = :month)
      AND (:day IS NULL OR DAY(m.createdDate) = :day)
  """)
  long countTotalMatchings(@Param("year") int year, @Param("month") Integer month, @Param("day") Integer day
  );

  @Query("SELECT m FROM Matching m JOIN FETCH m.workLog WHERE m.manager.id = :managerId AND m.status = :status ORDER BY m.workLog.createdDate DESC")
  Page<Matching> findAllWithWorkLogByManager_IdAndStatus(@Param("managerId") Long managerId, @Param("status") MatchingStatus status, Pageable pageable);
}
