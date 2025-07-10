package com.homeaid.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardStatsDto {

  // 매출 현황
  private long todayRevenue;
  private long weeklyRevenue;
  private long monthlyRevenue;
  private long netRevenue;

  // 환불 정보
  private long totalRefundAmount;
  private double refundRate; // (%)

  // 수익 분석
  private long platformProfit;
  private long managerSettlementAmount;

}
