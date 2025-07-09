package com.homeaid.statistics.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminStatisticsDto {
  private int year;
  private Integer month;
  private Integer day;

  private UserStatsDto userStats;
  private PaymentStatsDto paymentStats;
  private ReservationStatsDto reservationStats;
  private MatchingStatsDto matchingStats;
  private SettlementStatsDto settlementStats;
  private ManagerRatingStatsDto managerRatingStats;
}
