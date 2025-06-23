package com.homeaid.statistics.service;

import com.homeaid.statistics.dto.MatchingStatsDto;
import com.homeaid.statistics.dto.PaymentStatsDto;
import com.homeaid.statistics.dto.ReservationStatsDto;
import com.homeaid.statistics.dto.SettlementStatsDto;
import com.homeaid.statistics.dto.UserStatsDto;

public interface AdminStatisticsService {

  // 회원 통계
  UserStatsDto getUserStats(int year);
  UserStatsDto getUserStatsByMonth(int year, int month);
  UserStatsDto getWithdrawalStatsByMonth(int year, int month);

  // 정산 통계
  SettlementStatsDto getSettlementStats(int year);
  SettlementStatsDto getSettlementStatsByMonth(int year, int month);

  // 결제 통계
  PaymentStatsDto getPaymentStats(int year);
  PaymentStatsDto getPaymentStatsByMonth(int year, int month);

  // 예약 통계
  ReservationStatsDto getReservationStats(int year, Integer month);

  // 수익 통계
  //ProfitStatsDto getProfitStats(int year);

  // 이탈 통계
  UserStatsDto getWithdrawalStats(int year);

  // 매칭 통계
  MatchingStatsDto getMatchingSuccessStats(int year, Integer month);
  MatchingStatsDto getMatchingFailStats(int year, Integer month);

  // CS 통계
  //InquiryStatsDto getInquiryStats(int year);

  // 서비스 품질 통계
  //ManagerRatingStatsDto getManagerRatingStats(int year);
}
