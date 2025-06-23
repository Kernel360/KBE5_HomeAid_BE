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

  // 회원 통계
  @Override
  public UserStatsDto getUserStats(int year) {
    Double withdrawRate = userRepository.calculateWithdrawRate(year);
    return UserStatsDto.builder()
        .year(year)
        .signupCount(userRepository.countSignUpsByYear(year))
        .totalUsers(userRepository.count())
        .withdrawCount(userRepository.countWithdrawnByYear(year))
        .inactiveUserCount(userRepository.countInactiveUsers(LocalDate.now().minusMonths(6).atStartOfDay()))
        .withdrawRate(withdrawRate != null ? withdrawRate : 0.0)
        .build();
  }

  // 정산 통계
  @Override
  public SettlementStatsDto getSettlementStats(int year) {
    return SettlementStatsDto.builder()
        .year(year)
        .requestedCount(settlementRepository.countRequestedByYear(year))
        .paidCount(settlementRepository.countPaidByYear(year))
        .build();
  }

  // 결제 통계
  @Override
  public PaymentStatsDto getPaymentStats(int year) {
    return PaymentStatsDto.builder()
        .year(year)
        .totalAmount(paymentRepository.sumPaymentsByYear(year))
        .cancelAmount(paymentRepository.sumCanceledPaymentsByYear(year))
        .build();
  }

  @Override
  public PaymentStatsDto getPaymentMethodStats(int year, int month) {
    Object result = paymentRepository.findPaymentMethodSums(year, month);

    long card = 0L;
    long transfer = 0L;
    long cash = 0L;

    if (result != null) {
      Object[] values = (Object[]) result;

      if (values.length == 3) {
        if (values[0] instanceof Number) card = ((Number) values[0]).longValue();
        if (values[1] instanceof Number) transfer = ((Number) values[1]).longValue();
        if (values[2] instanceof Number) cash = ((Number) values[2]).longValue();
      }
    }

    long total = paymentRepository.sumPaymentsByYear(year);
    long canceled = paymentRepository.sumCanceledPaymentsByYear(year);

    return PaymentStatsDto.builder()
        .year(year)
        .month(month)
        .card(card)
        .transfer(transfer)
        .cash(cash)
        .totalAmount(total)
        .cancelAmount(canceled)
        .build();
  }


  // 예약 통계
  @Override
  public ReservationStatsDto getReservationStats(int year) {
    return ReservationStatsDto.builder()
        .year(year)
        .reservationCount(reservationRepository.countByYear(year))
        .avgProcessingMinutes(reservationRepository.getAverageProcessingDays(year))
        .build();
  }

  // 이탈 통계
  @Override
  public UserStatsDto getWithdrawalStats(int year) {
    Double withdrawRate = userRepository.calculateWithdrawRate(year);

    return UserStatsDto.builder()
        .year(year)
        .withdrawRate(withdrawRate != null ? withdrawRate : 0.0)
        .build();
  }

  // 매칭 통계
  @Override
  public MatchingStatsDto getMatchingSuccessStats(int year) {
    return MatchingStatsDto.builder()
        .year(year)
        .successCount(matchingRepository.countSuccess(year))
        .build();
  }

  @Override
  public MatchingStatsDto getMatchingFailStats(int year) {
    return MatchingStatsDto.builder()
        .year(year)
        .failCount(matchingRepository.countFailOrCancel(year))
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
