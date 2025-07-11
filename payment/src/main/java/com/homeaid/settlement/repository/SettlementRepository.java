package com.homeaid.settlement.repository;

import com.homeaid.settlement.domain.Settlement;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

  // 매니저
  List<Settlement> findAllByManagerId(Long managerId);
  List<Settlement> findBySettlementWeekStart(LocalDate startDate);
  List<Settlement> findByManagerIdAndSettlementWeekStart(Long managerId, LocalDate settlementWeekStart);
  List<Settlement> findByManagerIdAndSettlementWeekStartLessThanEqualAndSettlementWeekEndGreaterThanEqual(
      Long managerId, LocalDate end, LocalDate start
  );

  // 관리자
  List<Settlement> findBySettlementWeekStartBetween(LocalDate start, LocalDate end); //주간 정산 조회시 필요
  boolean existsByManagerIdAndSettlementWeekStartAndSettlementWeekEnd(
      Long managerId, LocalDate settlementWeekStart, LocalDate settlementWeekEnd);
  // 특정 매니저가 특정 주차(weekStart~weekEnd)에 이미 정산 생성되었는지 여부 확인 - 중복 정산 방지 검증


  // 전체 정산 합계 조회
  @Query("SELECT COALESCE(SUM(s.managerSettlementPrice), 0) FROM Settlement s")
  long sumAllSettlementAmounts();

  // 정산 신청 수 (연/월/일)
  @Query("""
    SELECT COUNT(s) FROM Settlement s
    WHERE YEAR(s.settledAt) = :year
      AND (:month IS NULL OR MONTH(s.settledAt) = :month)
      AND (:day IS NULL OR DAY(s.settledAt) = :day)
  """)
  long countRequested(@Param("year") int year, @Param("month") Integer month, @Param("day") Integer day
  );

  // 정산 지급 완료 수 (연/월/일)
  @Query("""
    SELECT COUNT(s) FROM Settlement s
    WHERE s.paidAt IS NOT NULL
      AND YEAR(s.paidAt) = :year
      AND (:month IS NULL OR MONTH(s.paidAt) = :month)
      AND (:day IS NULL OR DAY(s.paidAt) = :day)
  """)
  long countPaid(@Param("year") int year, @Param("month") Integer month, @Param("day") Integer day
  );

}
