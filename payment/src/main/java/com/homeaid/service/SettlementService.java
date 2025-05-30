package com.homeaid.service;

import com.homeaid.dto.response.SettlementResponseDto;
import java.time.LocalDate;

public interface SettlementService {
  SettlementResponseDto createWeeklySettlementForManager(Long managerId, LocalDate weekStart, LocalDate weekEnd);

}
