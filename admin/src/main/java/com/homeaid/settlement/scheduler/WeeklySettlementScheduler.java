package com.homeaid.settlement.scheduler;

import com.homeaid.settlement.service.AdminSettlementService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeeklySettlementScheduler {

  private final AdminSettlementService adminSettlementService;

  @Scheduled(cron = "0 0 1 * * Mon") //
  //@Scheduled(cron = "0 50 18 * * *")  // 매일 18:40 (오후 6시 40분) 테스트
  public void createWeeklySettlements() {
    log.info("[정산 스케줄러] 주간 정산 생성 시작");

    // 현재 주차 기준: 지난주 월~일 계산
    LocalDate today = LocalDate.now();
    LocalDate weekStart = today.minusWeeks(1).with(DayOfWeek.MONDAY);
    LocalDate weekEnd = weekStart.plusDays(6);

    adminSettlementService.createSettlementsForAllManagers(weekStart, weekEnd);

    log.info("[정산 스케줄러] 주간 정산 생성 완료 ({} ~ {})", weekStart, weekEnd);
  }

}
