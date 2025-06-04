package com.homeaid.service;

import com.homeaid.domain.Settlement;
import com.homeaid.dto.response.SettlementResponseDto;
import java.time.LocalDate;
import java.util.List;

public interface SettlementService {
  SettlementResponseDto createWeeklySettlementForManager(Long managerId, LocalDate weekStart, LocalDate weekEnd);

  List<Settlement> findAll();

  Settlement findById(Long settlementId);

  List<Settlement> findByManagerId(Long managerId);
}
