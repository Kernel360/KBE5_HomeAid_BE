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

  // 관리지
  List<Settlement> findBySettlementWeekStartBetween(LocalDate start, LocalDate end); //주간 정산 조회시 필요

  // 정산 신청 수 (연간 또는 월간)
  @Query("SELECT COUNT(s) FROM Settlement s WHERE YEAR(s.settledAt) = :year AND (:month IS NULL OR MONTH(s.settledAt) = :month)")
  long countRequested(@Param("year") int year, @Param("month") Integer month);

  // 정산 지급 완료 수 (연간 또는 월간)
  @Query("SELECT COUNT(s) FROM Settlement s WHERE s.paidAt IS NOT NULL AND YEAR(s.paidAt) = :year AND (:month IS NULL OR MONTH(s.paidAt) = :month)")
  long countPaid(@Param("year") int year, @Param("month") Integer month);
}
