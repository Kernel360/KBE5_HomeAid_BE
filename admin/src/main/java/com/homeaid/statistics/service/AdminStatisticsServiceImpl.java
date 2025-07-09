package com.homeaid.statistics.service;

import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.repository.MatchingRepository;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.repository.ReviewRepository;
import com.homeaid.repository.UserRepository;
import com.homeaid.settlement.repository.SettlementRepository;
import com.homeaid.statistics.dto.ManagerRatingStatsDto;
import com.homeaid.statistics.dto.MatchingStatsDto;
import com.homeaid.statistics.dto.PaymentStatsDto;
import com.homeaid.statistics.dto.ReservationStatsDto;
import com.homeaid.statistics.dto.SettlementStatsDto;
import com.homeaid.statistics.dto.UserStatsDto;
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

}