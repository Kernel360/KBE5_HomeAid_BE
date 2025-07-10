package com.homeaid.statistics.provider;

import com.homeaid.statistics.dto.PaymentStatsDto;

public interface PaymentStatisticsProvider {
  PaymentStatsDto generate(int year, Integer month, Integer day);
}
