package com.homeaid.dashboard.service;

import com.homeaid.dashboard.dto.AdminDashboardStatsDto;
import com.homeaid.dashboard.dto.DailyRevenueRefundDto;
import java.util.List;

public interface AdminDashboardService {
  AdminDashboardStatsDto getStats();
  List<DailyRevenueRefundDto> getLast7DaysRevenueAndRefundStats();
}
