package com.homeaid.statistics.service;

import com.homeaid.exception.CustomException;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.repository.MatchingRepository;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.repository.ReviewRepository;
import com.homeaid.repository.UserRepository;
import com.homeaid.settlement.repository.SettlementRepository;
import com.homeaid.statistics.domain.StatisticsEntity;
import com.homeaid.statistics.dto.AdminStatisticsDto;
import com.homeaid.statistics.dto.ManagerRatingStatsDto;
import com.homeaid.statistics.dto.MatchingStatsDto;
import com.homeaid.statistics.dto.PaymentStatsDto;
import com.homeaid.statistics.dto.ReservationStatsDto;
import com.homeaid.statistics.dto.SettlementStatsDto;
import com.homeaid.statistics.dto.UserStatsDto;
import com.homeaid.statistics.exception.StatisticsErrorCode;
import com.homeaid.statistics.repository.StatisticsRepository;
import com.homeaid.util.RedisKeyFactory;
import com.homeaid.util.RedisUtil;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

  private final UserRepository userRepository;
  private final PaymentRepository paymentRepository;
  private final ReservationRepository reservationRepository;
  private final SettlementRepository settlementRepository;
  private final MatchingRepository matchingRepository;
  private final ReviewRepository reviewRepository; // 매니저 전체 평균 평점, 매니저 대상 총 리뷰 수

  private final RedisUtil redisUtil;
  private final StatisticsRepository statisticsRepository;

  @Override
  public UserStatsDto getUserStats(int year, Integer month, Integer day) {
    return UserStatsDto.builder()
        .year(year)
        .month(month)
        .day(day)
        .signupCount(userRepository.countSignUps(year, month, day))
        .totalUsers(userRepository.count())
        .withdrawCount(userRepository.countWithdrawn(year, month, day))
        .build();
  }

  @Override
  public SettlementStatsDto getSettlementStats(int year, Integer month, Integer day) {
    return SettlementStatsDto.builder()
        .year(year).month(month).day(day)
        .requestedCount(settlementRepository.countRequested(year, month, day))
        .paidCount(settlementRepository.countPaid(year, month, day))
        .build();
  }

  @Override
  public PaymentStatsDto getPaymentStats(int year, Integer month, Integer day) {
    long total = paymentRepository.sumPayments(year, month, day);
    long canceled = paymentRepository.sumCanceledPayments(year, month, day);
    long paymentCount = paymentRepository.countPayments(year, month, day);
    long cancelCount = paymentRepository.countCanceledPayments(year, month, day);

    PaymentStatsDto.PaymentStatsDtoBuilder builder = PaymentStatsDto.builder()
        .year(year).month(month).day(day)
        .totalAmount(total)
        .cancelAmount(canceled)
        .paymentCount(paymentCount)
        .cancelCount(cancelCount);

    // 월 통계일 경우에만 수단별 통계 추가
    if (month != null && day == null) {
      applyPaymentMethodSums(builder, year, month);
    }

    return builder.build();
  }

  private void applyPaymentMethodSums(PaymentStatsDto.PaymentStatsDtoBuilder builder, int year, int month) {
    Object result = paymentRepository.findPaymentMethodSums(year, month);
    if (result instanceof Object[] values && values.length == 3) {
      builder.card(((Number) values[0]).longValue());
      builder.transfer(((Number) values[1]).longValue());
      builder.cash(((Number) values[2]).longValue());
    } else {
      builder.card(0L).transfer(0L).cash(0L);
    }
  }

  @Override
  public ReservationStatsDto getReservationStats(int year, Integer month, Integer day) {
    long reservationCount = reservationRepository.countReservations(year, month, day);
    long cancelledCount = reservationRepository.countCancelledReservations(year, month, day);
    Double avgMinutes = reservationRepository.getAverageProcessingMinutes(year, month, day);
    long completed = reservationRepository.countCompletedReservations(year, month, day);

    double successRate = (reservationCount > 0)
        ? (completed * 100.0 / reservationCount)
        : 0.0;

    return ReservationStatsDto.builder()
        .year(year).month(month).day(day)
        .reservationCount(reservationCount)
        .cancelledCount(cancelledCount)
        .avgProcessingMinutes(avgMinutes != null ? avgMinutes : 0.0)
        .reservationSuccessRate(successRate)
        .build();
  }

  @Override
  public MatchingStatsDto getMatchingStats(int year, Integer month, Integer day) {
    long success = matchingRepository.countConfirmedMatchings(year, month, day);
    long fail = matchingRepository.countFailedOrCancelledMatchings(year, month, day);
    long total = matchingRepository.countTotalMatchings(year, month, day);

    return MatchingStatsDto.builder()
        .year(year).month(month).day(day)
        .successCount(success)
        .failCount(fail)
        .totalCount(total)
        .build();
  }

  @Override
  public ManagerRatingStatsDto getManagerRatingStats(int year, Integer month, Integer day) {
    Double avg = reviewRepository.findAvgRatingForManagers(year, month, day);
    Long count = reviewRepository.countReviewsForManagers(year, month, day);

    return ManagerRatingStatsDto.builder()
        .year(year).month(month).day(day)
        .avgRating(avg != null ? avg : 0.0)
        .reviewCount(count != null ? count : 0L)
        .build();
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
    redisUtil.save(key, dto, Duration.ofDays(30));

    StatisticsEntity entity = StatisticsEntity.fromDto(dto);
    statisticsRepository.save(entity);
  }

  /**
   * Redis 조회 후 없으면 DB fallback (Controller에서 활용)
   */
  public AdminStatisticsDto getStatisticsOrLoad(int year, Integer month, Integer day) {
    String key = RedisKeyFactory.buildAdminStatisticsKey(year, month, day);

    // 1. Redis 우선 조회
    Object cached = redisUtil.getObject(key);
    if (cached instanceof AdminStatisticsDto cachedDto) {
      return cachedDto;
    }

    // 2. Redis에 없다면 DB에서 조회
    return statisticsRepository.findByYearAndMonthAndDay(year, month, day)
        .map(StatisticsEntity::toDto) // 내부에서 직렬화 예외 → CustomException 처리됨
        .orElseThrow(() -> new CustomException(StatisticsErrorCode.STATISTICS_NOT_FOUND));
  } // 추천: 추가 모니터링을 위한 메트릭 연동 (선택사항)

  /**
   * 스케줄러나 초기화 시에 호출: 통계 생성 → 저장
   */
  public void generateAndStoreStatistics(int year, Integer month, Integer day) {
    AdminStatisticsDto dto = generateStatistics(year, month, day);
    saveStatisticsToRedisAndDb(dto);
  }

}