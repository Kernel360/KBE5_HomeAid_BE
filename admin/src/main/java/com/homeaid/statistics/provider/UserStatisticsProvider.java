package com.homeaid.statistics.provider;

import com.homeaid.statistics.dto.UserStatsDto;

public interface UserStatisticsProvider {
  UserStatsDto generate(int year, Integer month, Integer day);
}
