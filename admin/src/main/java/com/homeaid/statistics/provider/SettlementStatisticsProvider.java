package com.homeaid.statistics.provider;

import com.homeaid.statistics.dto.SettlementStatsDto;

public interface SettlementStatisticsProvider {
  SettlementStatsDto generate(int year, Integer month, Integer day);
}
