package com.homeaid.statistics.provider.impl;

import com.homeaid.settlement.repository.SettlementRepository;
import com.homeaid.statistics.dto.SettlementStatsDto;
import com.homeaid.statistics.provider.SettlementStatisticsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettlementStatisticsProviderImpl implements SettlementStatisticsProvider {

  private final SettlementRepository settlementRepository;

  @Override
  public SettlementStatsDto generate(int year, Integer month, Integer day) {
    long requested = settlementRepository.countRequested(year, month, day);
    long paid = settlementRepository.countPaid(year, month, day);

    return SettlementStatsDto.builder()
        .year(year)
        .month(month)
        .day(day)
        .requestedCount(requested)
        .paidCount(paid)
        .build();
  }
}
