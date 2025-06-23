package com.homeaid.statistics.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.statistics.dto.ManagerRatingStatsDto;
import com.homeaid.statistics.dto.MatchingStatsDto;
import com.homeaid.statistics.dto.PaymentStatsDto;
import com.homeaid.statistics.dto.ReservationStatsDto;
import com.homeaid.statistics.dto.SettlementStatsDto;
import com.homeaid.statistics.dto.UserStatsDto;
import com.homeaid.statistics.service.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatisticsController {

  private final AdminStatisticsService adminStatisticsService;

  // 회원 통계 전체 조회
  @GetMapping("/users")
  public UserStatsDto getUserStatistics(@RequestParam int year) {
    return adminStatisticsService.getUserStats(year);
  }

  // 정산 통계
  @GetMapping("/settlements")
  public SettlementStatsDto getSettlementStatistics(@RequestParam int year) {
    return adminStatisticsService.getSettlementStats(year);
  }

  // 결제 통계
  @GetMapping("/payments")
  public PaymentStatsDto getPaymentStatistics(@RequestParam int year) {
    return adminStatisticsService.getPaymentStats(year);
  }

  // 결제 수단별 매출 통계
  @GetMapping("/payments/methods")
  public PaymentStatsDto getPaymentMethodStatistics(@RequestParam int year, @RequestParam int month) {
    return adminStatisticsService.getPaymentMethodStats(year, month);
  }

  // 예약 통계
  @GetMapping("/reservations")
  public ReservationStatsDto getReservationStatistics(@RequestParam int year) {
    return adminStatisticsService.getReservationStats(year);
  }

  // 이탈 통계 - 탈퇴율
  @GetMapping("/withdraw-rate")
  public UserStatsDto getWithdrawRate(@RequestParam int year) {
    return adminStatisticsService.getWithdrawalStats(year);
  }

  // 매칭 통계 - 성공
  @GetMapping("/matching/success")
  public MatchingStatsDto getMatchingSuccessStats(@RequestParam int year) {
    return adminStatisticsService.getMatchingSuccessStats(year);
  }

  // 매칭 통계 - 실패/취소
  @GetMapping("/matching/fail")
  public MatchingStatsDto getMatchingFailStats(@RequestParam int year) {
    return adminStatisticsService.getMatchingFailStats(year);
  }

  // 서비스 품질 통계 (평균 평점)
//    @GetMapping("/manager/ratings")
//    public ManagerRatingStatsDto getManagerRatingStats(@RequestParam int year) {
//        return statisticsService.getManagerRatingStats(year);
//    }
}
