package com.homeaid.settlement.service;

import com.homeaid.settlement.domain.Settlement;
import com.homeaid.settlement.repository.SettlementRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

  private final SettlementRepository settlementRepository;

  @Override
  public List<Settlement> getWeeklySettlements(Long managerId, LocalDate startDate) {
    return settlementRepository.findByManagerIdAndSettlementWeekStart(managerId, startDate);
  }
}