package com.homeaid.manager.dto;

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
  private long todayReservations;
  private long pendingApprovals;
}
