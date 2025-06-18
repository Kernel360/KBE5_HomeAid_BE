package com.homeaid.settlement.service;

import com.homeaid.settlement.domain.Settlement;
import java.time.LocalDate;
import java.util.List;

public interface SettlementService {
  List<Settlement> getWeeklySettlements(Long managerId, LocalDate startDate);
}
