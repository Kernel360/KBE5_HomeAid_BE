package com.homeaid.statistics.service;

import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.repository.MatchingRepository;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.repository.UserRepository;
import com.homeaid.settlement.repository.SettlementRepository;
import com.homeaid.statistics.dto.MatchingStatsDto;
import com.homeaid.statistics.dto.PaymentStatsDto;
import com.homeaid.statistics.dto.ReservationStatsDto;
import com.homeaid.statistics.dto.SettlementStatsDto;
import com.homeaid.statistics.dto.UserStatsDto;
import java.time.LocalDate;
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
  //private final ReviewRepository reviewRepository;

  // 회원 통계 - 연도/월 파라미터 공통 처리
  private UserStatsDto buildUserStats(int year, Integer month) {
    Double withdrawRate = userRepository.calculateWithdrawRate(year, month);
    return UserStatsDto.builder()
        .year(year)
        .month(month)
        .signupCount(userRepository.countSignUps(year, month))
        .totalUsers(userRepository.count())
        .withdrawCount(userRepository.countWithdrawn(year, month))
        .inactiveUserCount(userRepository.countInactiveUsers(LocalDate.now().minusMonths(6).atStartOfDay()))
        .withdrawRate(withdrawRate != null ? withdrawRate : 0.0)
        .build();
  }

  @Override
  public UserStatsDto getUserStats(int year) {
    return buildUserStats(year, null);
  }

  @Override
  public UserStatsDto getUserStatsByMonth(int year, int month) {
    return buildUserStats(year, month);
  }

  @Override
  public UserStatsDto getWithdrawalStatsByMonth(int year, int month) {
    Double withdrawRate = userRepository.calculateWithdrawRate(year, month);
    return UserStatsDto.builder()
        .year(year)
        .month(month)
        .withdrawRate(withdrawRate != null ? withdrawRate : 0.0)
        .build();
  }

  // 이탈 통계
  @Override
  public UserStatsDto getWithdrawalStats(int year) {
    Double withdrawRate = userRepository.calculateWithdrawRate(year, null);
    return UserStatsDto.builder()
        .year(year)
        .withdrawRate(withdrawRate != null ? withdrawRate : 0.0)
        .build();
  }

  // 정산 통계
  @Override
  public SettlementStatsDto getSettlementStats(int year) {
    return buildSettlementStats(year, null);
  }

  @Override
  public SettlementStatsDto getSettlementStatsByMonth(int year, int month) {
    return buildSettlementStats(year, month);
  }

  private SettlementStatsDto buildSettlementStats(int year, Integer month) {
    return SettlementStatsDto.builder()
        .year(year)
        .month(month)
        .requestedCount(settlementRepository.countRequested(year, month))
        .paidCount(settlementRepository.countPaid(year, month))
        .build();
  }

  // 결제 통계
  @Override
  public PaymentStatsDto getPaymentStats(int year) {
    return buildPaymentStats(year, null);
  }

  @Override
  public PaymentStatsDto getPaymentStatsByMonth(int year, int month) {
    return buildPaymentStats(year, month);
  }

  // 공통 메서드
  private PaymentStatsDto buildPaymentStats(int year, Integer month) {
    long total = paymentRepository.sumPayments(year, month);
    long canceled = paymentRepository.sumCanceledPayments(year, month);

    PaymentStatsDto.PaymentStatsDtoBuilder builder = PaymentStatsDto.builder()
        .year(year)
        .month(month)
        .totalAmount(total)
        .cancelAmount(canceled);

    // 월별일 경우만 수단별 통계 추가
    if (month != null) {
      applyPaymentMethodSums(builder, year, month);
    }

    return builder.build();
  }

  private void applyPaymentMethodSums(PaymentStatsDto.PaymentStatsDtoBuilder builder, int year, int month) {
    Object result = paymentRepository.findPaymentMethodSums(year, month);

    if (result instanceof Object[] values && values.length == 3) {
      if (values[0] instanceof Number card) builder.card(card.longValue());
      if (values[1] instanceof Number transfer) builder.transfer(transfer.longValue());
      if (values[2] instanceof Number cash) builder.cash(cash.longValue());
    } else {
      builder.card(0L).transfer(0L).cash(0L);
    }
  }

  // 예약 통계
  @Override
  public ReservationStatsDto getReservationStats(int year, Integer month) {
    return ReservationStatsDto.builder()
        .year(year)
        .month(month)
        .reservationCount(reservationRepository.countReservationsByYearAndOptionalMonth(year, month))
        .avgProcessingMinutes(reservationRepository.getAverageProcessingDays(year, month))
        .build();
  }

  // 매칭 통계
  @Override
  public MatchingStatsDto getMatchingSuccessStats(int year, Integer month) {
    return MatchingStatsDto.builder()
        .year(year)
        .month(month)
        .successCount(matchingRepository.countConfirmedMatchings(year, month))
        .build();
  }

  @Override
  public MatchingStatsDto getMatchingFailStats(int year, Integer month) {
    return MatchingStatsDto.builder()
        .year(year)
        .month(month)
        .failCount(matchingRepository.countFailedOrCancelledMatchings(year, month))
        .build();
  }

  // 서비스 품질 통계
//  @Override
//  public ManagerRatingStatsDto getManagerRatingStats(int year) {
//    return ManagerRatingStatsDto.builder()
//        .year(year)
//        .avgRating(reviewRepository.getAverageRatingByYear(year))
//        .build();
//  }
}