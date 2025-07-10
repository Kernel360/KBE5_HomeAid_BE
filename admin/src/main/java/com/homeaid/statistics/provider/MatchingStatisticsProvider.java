package com.homeaid.statistics.provider;

import com.homeaid.statistics.dto.MatchingStatsDto;

public interface MatchingStatisticsProvider {
  MatchingStatsDto generate(int year, Integer month, Integer day);
}
