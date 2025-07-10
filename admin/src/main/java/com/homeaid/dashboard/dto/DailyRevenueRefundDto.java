package com.homeaid.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DailyRevenueRefundDto {
  private String date; // 예: "2025-07-10"
  private long paymentAmount;
  private long refundAmount;
}