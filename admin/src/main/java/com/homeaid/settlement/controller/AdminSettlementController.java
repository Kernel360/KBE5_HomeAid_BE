package com.homeaid.settlement.controller;

import com.homeaid.service.SettlementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/settlement")
@Tag(name = "AdminSettlement", description = "관리자 정산 API")
public class AdminSettlementController {
  private final SettlementService settlementService;

  // 매니저별 정산 요청 API
  @PostMapping("/manager")
  public ResponseEntity<Void> createManagerSettlement(
      @RequestParam Long managerId,
      @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate weekStart,
      @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate weekEnd
  ) {
    settlementService.createWeeklySettlementForManager(managerId, weekStart, weekEnd);
    return ResponseEntity.ok().build();
  }
}