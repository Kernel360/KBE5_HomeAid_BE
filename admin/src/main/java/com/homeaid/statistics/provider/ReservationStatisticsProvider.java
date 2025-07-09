package com.homeaid.statistics.provider;

import com.homeaid.statistics.dto.ReservationStatsDto;

public interface ReservationStatisticsProvider {
  ReservationStatsDto generate(int year, Integer month, Integer day);
}
