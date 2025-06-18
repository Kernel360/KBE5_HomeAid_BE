package com.homeaid.settlement.repository;

import com.homeaid.settlement.domain.Settlement;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

  // 매니저
  List<Settlement> findAllByManagerId(Long managerId);
  List<Settlement> findBySettlementWeekStart(LocalDate startDate);
  List<Settlement> findByManagerIdAndSettlementWeekStart(Long managerId, LocalDate settlementWeekStart);

  // 관리지
  List<Settlement> findBySettlementWeekStartBetween(LocalDate start, LocalDate end); //주간 정산 조회시 필요
}
