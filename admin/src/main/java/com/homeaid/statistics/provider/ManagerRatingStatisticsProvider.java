package com.homeaid.statistics.provider;

import com.homeaid.statistics.dto.ManagerRatingStatsDto;

public interface ManagerRatingStatisticsProvider {
  ManagerRatingStatsDto generate(int year, Integer month, Integer day);
}
