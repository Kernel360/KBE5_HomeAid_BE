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

  @Query("SELECT COUNT(s) FROM Settlement s WHERE YEAR(s.settledAt) = :year")
  long countRequestedByYear(@Param("year") int year);

  @Query("SELECT COUNT(s) FROM Settlement s WHERE s.paidAt IS NOT NULL AND YEAR(s.paidAt) = :year")
  long countPaidByYear(@Param("year") int year);
}
