package com.homeaid.statistics.service;

import com.homeaid.statistics.dto.AdminStatisticsDto;
import com.homeaid.statistics.dto.ManagerRatingStatsDto;
import com.homeaid.statistics.dto.MatchingStatsDto;
import com.homeaid.statistics.dto.PaymentStatsDto;
import com.homeaid.statistics.dto.ReservationStatsDto;
import com.homeaid.statistics.dto.SettlementStatsDto;
import com.homeaid.statistics.dto.UserStatsDto;
import com.homeaid.statistics.provider.ManagerRatingStatisticsProvider;
import com.homeaid.statistics.provider.MatchingStatisticsProvider;
import com.homeaid.statistics.provider.PaymentStatisticsProvider;
import com.homeaid.statistics.provider.ReservationStatisticsProvider;
import com.homeaid.statistics.provider.SettlementStatisticsProvider;
import com.homeaid.statistics.provider.UserStatisticsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

  private final UserStatisticsProvider userStatisticsProvider;
  private final PaymentStatisticsProvider paymentStatisticsProvider;
  private final ReservationStatisticsProvider reservationStatisticsProvider;
  private final MatchingStatisticsProvider matchingStatisticsProvider;
  private final SettlementStatisticsProvider settlementStatisticsProvider;
  private final ManagerRatingStatisticsProvider managerRatingStatisticsProvider;

  // Redis & DB 저장/조회 책임 분리
  private final AdminStatisticsStorageService storageService;

  @Override
  public UserStatsDto getUserStats(int year, Integer month, Integer day) {
    return userStatisticsProvider.generate(year, month, day);
  }

  // 정산 통계
  @Override
  public PaymentStatsDto getPaymentStats(int year, Integer month, Integer day) {
    return paymentStatisticsProvider.generate(year, month, day);
  }

  @Override
  public ReservationStatsDto getReservationStats(int year, Integer month, Integer day) {
    return reservationStatisticsProvider.generate(year, month, day);
  }

  @Override
  public MatchingStatsDto getMatchingStats(int year, Integer month, Integer day) {
    return matchingStatisticsProvider.generate(year, month, day);
  }

  @Override
  public SettlementStatsDto getSettlementStats(int year, Integer month, Integer day) {
    return settlementStatisticsProvider.generate(year, month, day);
  }

  @Override
  public ManagerRatingStatsDto getManagerRatingStats(int year, Integer month, Integer day) {
    return managerRatingStatisticsProvider.generate(year, month, day);
  }

  /**
   * 연/월/일 기준 전체 통계를 한 번에 생성하는 통합 메서드
   * Redis 저장 또는 Fallback 처리 시 공통 호출 용도로 사용됨
   */
  public AdminStatisticsDto generateStatistics(int year, Integer month, Integer day) {
    return AdminStatisticsDto.builder()
        .year(year).month(month).day(day)
        .userStats(getUserStats(year, month, day))
        .paymentStats(getPaymentStats(year, month, day))
        .reservationStats(getReservationStats(year, month, day))
        .matchingStats(getMatchingStats(year, month, day))
        .settlementStats(getSettlementStats(year, month, day))
        .managerRatingStats(getManagerRatingStats(year, month, day))
        .build();
  }

  /**
   * Redis에서 조회, 없으면 DB fallback
   */
  @Override
  public AdminStatisticsDto getStatisticsOrLoad(int year, Integer month, Integer day) {
    return storageService.loadOrThrow(year, month, day);
  }

  /**
   * Redis → DB 저장 (스케줄러 호출 시)
   */
  public void generateAndStoreStatistics(int year, Integer month, Integer day) {
    AdminStatisticsDto dto = generateStatistics(year, month, day);
    storageService.save(dto);
  }

}
