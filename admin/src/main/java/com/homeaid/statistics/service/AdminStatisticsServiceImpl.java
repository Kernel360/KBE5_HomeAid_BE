package com.homeaid.statistics.service;

import static com.homeaid.statistics.config.StatisticsConstants.STATISTICS_CACHE_TTL;

import com.homeaid.exception.CustomException;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.repository.MatchingRepository;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.repository.ReviewRepository;
import com.homeaid.repository.UserRepository;
import com.homeaid.settlement.repository.SettlementRepository;
import com.homeaid.statistics.config.StatisticsConstants;
import com.homeaid.statistics.domain.StatisticsEntity;
import com.homeaid.statistics.dto.AdminStatisticsDto;
import com.homeaid.statistics.dto.ManagerRatingStatsDto;
import com.homeaid.statistics.dto.MatchingStatsDto;
import com.homeaid.statistics.dto.PaymentStatsDto;
import com.homeaid.statistics.dto.ReservationStatsDto;
import com.homeaid.statistics.dto.SettlementStatsDto;
import com.homeaid.statistics.dto.UserStatsDto;
import com.homeaid.statistics.exception.StatisticsErrorCode;
import com.homeaid.statistics.provider.ManagerRatingStatisticsProvider;
import com.homeaid.statistics.provider.MatchingStatisticsProvider;
import com.homeaid.statistics.provider.PaymentStatisticsProvider;
import com.homeaid.statistics.provider.ReservationStatisticsProvider;
import com.homeaid.statistics.provider.SettlementStatisticsProvider;
import com.homeaid.statistics.provider.UserStatisticsProvider;
import com.homeaid.statistics.repository.StatisticsRepository;
import com.homeaid.util.RedisKeyFactory;
import com.homeaid.util.RedisUtil;
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

  private final ReviewRepository reviewRepository; // 매니저 전체 평균 평점, 매니저 대상 총 리뷰 수

  private final RedisUtil redisUtil;
  private final StatisticsRepository statisticsRepository;

  @Override
  public UserStatsDto getUserStats(int year, Integer month, Integer day) {
    return userStatisticsProvider.generate(year, month, day);
  }

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
   * 통계 데이터를 Redis에 저장하고, DB에도 백업 (매일 스케줄러에서 호출)
   * - Redis TTL 30일
   * - DB에는 JSON으로 저장 (StatisticsEntity)
   */
  public void saveStatisticsToRedisAndDb(AdminStatisticsDto dto) {
    String key = RedisKeyFactory.buildAdminStatisticsKey(dto.getYear(), dto.getMonth(), dto.getDay());
    redisUtil.save(key, dto, STATISTICS_CACHE_TTL); // Redis TTL은 상수로 관리하여 유지보수 용이하게 설정

    StatisticsEntity entity = StatisticsEntity.fromDto(dto);
    statisticsRepository.save(entity);
  }

  /**
   * Redis 조회 후 없으면 DB fallback (Controller에서 활용)
   */
  @Override
  public AdminStatisticsDto getStatisticsOrLoad(int year, Integer month, Integer day) {
    String key = RedisKeyFactory.buildAdminStatisticsKey(year, month, day);

    // 1. Redis 우선 조회
    Object cached = redisUtil.getObject(key);
    if (cached instanceof AdminStatisticsDto cachedDto) {
      log.info("[캐시 HIT] Redis에서 통계 데이터 조회 - key: {}", key);
      return cachedDto;
    }

    if (cached != null) {
      log.warn("[캐시 MISS] Redis 값은 존재하지만 AdminStatisticsDto 타입이 아님 - key: {}", key);
    } else {
      log.warn("[캐시 MISS] Redis에 해당 통계 데이터 없음 - key: {}", key);
    }

    // 2. DB fallback
    return statisticsRepository.findByYearAndMonthAndDay(year, month, day)
        .map(entity -> {
          try {
            AdminStatisticsDto dto = entity.toDto();
            log.info("[DB Fallback] DB에서 통계 데이터를 조회하여 복원함 - key: {}", key);

            // ✅ Redis 재캐싱 (TTL: 30일)
            redisUtil.save(key, dto, StatisticsConstants.STATISTICS_CACHE_TTL);

            return dto;

          } catch (Exception e) {
            log.error("[DB Fallback 실패] 통계 JSON 역직렬화 실패 - key: {}", key, e);
            throw new CustomException(StatisticsErrorCode.STATISTICS_DESERIALIZATION_FAILED);
          }
        })
        .orElseThrow(() -> {
          log.error("[DB Fallback 실패] DB에 해당 날짜 통계 데이터 없음 - key: {}", key);
          return new CustomException(StatisticsErrorCode.STATISTICS_NOT_FOUND);
        });
  }


  /**
   * 스케줄러나 초기화 시에 호출: 통계 생성 → 저장
   */
  public void generateAndStoreStatistics(int year, Integer month, Integer day) {
    AdminStatisticsDto dto = generateStatistics(year, month, day);
    saveStatisticsToRedisAndDb(dto);
  }

}
