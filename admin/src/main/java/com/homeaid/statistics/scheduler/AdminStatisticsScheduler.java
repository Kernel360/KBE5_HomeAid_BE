package com.homeaid.statistics.scheduler;

import static com.homeaid.statistics.config.StatisticsConstants.STATISTICS_LOCK_TTL;

import com.homeaid.statistics.service.AdminStatisticsService;
import com.homeaid.util.RedisUtil;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminStatisticsScheduler {

  private static final String LOCK_PREFIX = "lock:statistics:";

  private final AdminStatisticsService adminStatisticsService;
  private final RedisUtil redisUtil;

  @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시
  public void runScheduledStatistics() {

    // 전날 기준으로 통계 생성 - 7월 9일 새벽 3시에 실행된 스케줄러는 7월 8일 통계를 계산해야 함
    LocalDate targetDate = LocalDate.now().minusDays(1);
    int year = targetDate.getYear();
    int month = targetDate.getMonthValue();
    int day = targetDate.getDayOfMonth();

    String lockKey = LOCK_PREFIX + year + ":" + month + ":" + day;

    // Redis 기반 분산 락 TTL (통계 중복 실행 방지용) → 상수화로 재사용성 확보
    boolean acquired = redisUtil.setIfAbsent(lockKey, "LOCKED", STATISTICS_LOCK_TTL);

    if (!acquired) {
      log.warn("[스케줄러] 이미 {}년 {}월 {}일 통계 작업이 실행 중입니다. 중복 실행 방지로 스킵합니다.", year, month, day);
      return;
    }

    log.info("[스케줄러] {}년 {}월 {}일 통계 계산 시작", year, month, day);

    try {
      // 통계 생성 및 저장
      adminStatisticsService.generateAndStoreStatistics(year, month, day);
      log.info("[스케줄러] {}년 {}월 {}일 통계 계산 완료", year, month, day);

    } catch (Exception e) {
      log.error("[스케줄러] 통계 생성 중 예외 발생: {}", e.getMessage(), e);
    }
  }

}
