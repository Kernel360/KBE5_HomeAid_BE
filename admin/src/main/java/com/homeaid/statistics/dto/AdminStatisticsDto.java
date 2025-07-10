package com.homeaid.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
