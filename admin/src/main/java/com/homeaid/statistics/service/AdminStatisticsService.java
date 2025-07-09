package com.homeaid.statistics.service;

import com.homeaid.statistics.dto.AdminStatisticsDto;
import com.homeaid.statistics.dto.ManagerRatingStatsDto;
import com.homeaid.statistics.dto.MatchingStatsDto;
import com.homeaid.statistics.dto.PaymentStatsDto;
import com.homeaid.statistics.dto.ReservationStatsDto;
import com.homeaid.statistics.dto.SettlementStatsDto;
import com.homeaid.statistics.dto.UserStatsDto;

public interface AdminStatisticsService {

  // 회원 통계
  UserStatsDto getUserStats(int year, Integer month, Integer day);

  // 정산 통계
  SettlementStatsDto getSettlementStats(int year, Integer month, Integer day);

  // 결제 통계
  PaymentStatsDto getPaymentStats(int year, Integer month, Integer day);

  // 예약 통계
  ReservationStatsDto getReservationStats(int year, Integer month, Integer day);

  // 매칭 통계
  MatchingStatsDto getMatchingStats(int year, Integer month, Integer day);

  // 서비스 품질 통계
  ManagerRatingStatsDto getManagerRatingStats(int year, Integer month, Integer day);

  // Redis 및 DB에 저장
  void saveStatisticsToRedisAndDb(AdminStatisticsDto dto);

  // Redis → DB fallback 통계 조회
  AdminStatisticsDto getStatisticsOrLoad(int year, Integer month, Integer day);

}
