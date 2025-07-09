package com.homeaid.statistics.scheduler;

import com.homeaid.statistics.dto.MatchingStatsDto;
import com.homeaid.statistics.dto.PaymentStatsDto;
import com.homeaid.statistics.dto.ReservationStatsDto;
import com.homeaid.statistics.dto.SettlementStatsDto;
import com.homeaid.statistics.dto.UserStatsDto;
import com.homeaid.statistics.service.AdminStatisticsService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminStatisticsScheduler {

  private final AdminStatisticsService adminStatisticsService;

  @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시 실행
  public void runScheduledStatistics() {
    LocalDate today = LocalDate.now();
    int year = today.getYear();
    int month = today.getMonthValue();
    int day = today.getDayOfMonth();

    log.info("[스케줄러] {}년 {}월 {}일 통계 계산 시작", year, month, day);

    try {
      log.info("→ 회원 통계 계산");
      UserStatsDto userStats = adminStatisticsService.getUserStats(year, month, day);
      log.info("회원 통계: {}", userStats);

      log.info("→ 결제 통계 계산");
      PaymentStatsDto paymentStats = adminStatisticsService.getPaymentStats(year, month, null); // 월간 통계
      log.info("결제 통계(월간): {}", paymentStats);

      log.info("→ 일별 결제 통계 계산");
      PaymentStatsDto dailyPaymentStats = adminStatisticsService.getPaymentStats(year, month, day); // 일별 통계
      log.info("결제 통계(일간): {}", dailyPaymentStats);

      log.info("→ 예약 통계 계산");
      ReservationStatsDto reservationStats = adminStatisticsService.getReservationStats(year, month, day);
      log.info("예약 통계: {}", reservationStats);

      log.info("→ 매칭 통계 계산");
      MatchingStatsDto matchingStats = adminStatisticsService.getMatchingStats(year, month, day);
      log.info("매칭 통계: {}", matchingStats);

      log.info("→ 정산 통계 계산");
      SettlementStatsDto settlementStats = adminStatisticsService.getSettlementStats(year, month, day);
      log.info("정산 통계: {}", settlementStats);

    } catch (Exception e) {
      log.error("스케줄링 통계 처리 중 오류 발생: {}", e.getMessage(), e);
    }

    log.info("[스케줄러] {}년 {}월 {}일 통계 계산 완료", year, month, day);
  }

}
