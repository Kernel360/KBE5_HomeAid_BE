package com.homeaid.manager.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.manager.dto.AdminDashboardStatsDto;
import com.homeaid.manager.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminDashboardController {

  private final AdminDashboardService adminDashboardService;

  @GetMapping("/api/v1/admin/dashboard-stats")
  public CommonApiResponse<AdminDashboardStatsDto> getAdminDashboardStats() {
    AdminDashboardStatsDto stats = adminDashboardService.getStats();
    return CommonApiResponse.success(stats);
  }

}
