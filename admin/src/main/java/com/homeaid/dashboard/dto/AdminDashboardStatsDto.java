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
  private long totalUsers;
  private long activeManagers;
  private long totalPayments;
  private long todayReservations;
  private long pendingApprovals;
}
