package com.homeaid.settlement.service;

import com.homeaid.settlement.domain.Settlement;
import java.time.LocalDate;
import java.util.List;

public interface SettlementService {
  Settlement createWeeklySettlementForManager(Long managerId, LocalDate weekStart, LocalDate weekEnd);

  List<Settlement> findAll();

  Settlement findById(Long settlementId);

  List<Settlement> findByManagerId(Long managerId);
}
