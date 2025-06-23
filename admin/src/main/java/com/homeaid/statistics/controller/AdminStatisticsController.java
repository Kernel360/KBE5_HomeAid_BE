package com.homeaid.statistics.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.statistics.dto.ManagerRatingStatsDto;
import com.homeaid.statistics.dto.MatchingStatsDto;
import com.homeaid.statistics.dto.PaymentStatsDto;
import com.homeaid.statistics.dto.ReservationStatsDto;
import com.homeaid.statistics.dto.SettlementStatsDto;
import com.homeaid.statistics.dto.UserStatsDto;
import com.homeaid.statistics.service.AdminStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  // TODO : 데이터가 Null 일 경우에 대한 방어로직 추가, 월 단위 추가
  // 회원 통계 전체 조회
  @GetMapping("/users")
  @Operation(summary = "연간 회원 통계")
  public ResponseEntity<CommonApiResponse<UserStatsDto>> getUserStatistics(@RequestParam int year) {
    return ResponseEntity.ok(CommonApiResponse.success(adminStatisticsService.getUserStats(year)));
  }

  // 정산 통계
  @GetMapping("/settlements")
  @Operation(summary = "연간 정산 통계")
  public ResponseEntity<CommonApiResponse<SettlementStatsDto>> getSettlementStatistics(@RequestParam int year) {
    return ResponseEntity.ok(CommonApiResponse.success(adminStatisticsService.getSettlementStats(year)));
  }

  // 결제 통계
  @GetMapping("/payments")
  @Operation(summary = "연간 결제 통계")
  public ResponseEntity<CommonApiResponse<PaymentStatsDto>> getPaymentStatistics(@RequestParam int year) {
    return ResponseEntity.ok(CommonApiResponse.success(adminStatisticsService.getPaymentStats(year)));
  }

  // 결제 수단별 매출 통계
  @GetMapping("/payments/methods")
  @Operation(summary = "월별 결제 수단별 통계")
  public ResponseEntity<CommonApiResponse<PaymentStatsDto>> getPaymentMethodStatistics(
      @RequestParam int year, @RequestParam int month) {
    return ResponseEntity.ok(CommonApiResponse.success(adminStatisticsService.getPaymentMethodStats(year, month)));
  }

  // 예약 통계
  @GetMapping("/reservations")
  @Operation(summary = "연간 예약 통계")
  public ResponseEntity<CommonApiResponse<ReservationStatsDto>> getReservationStatistics(@RequestParam int year) {
    return ResponseEntity.ok(CommonApiResponse.success(adminStatisticsService.getReservationStats(year)));
  }

  // 이탈 통계 - 탈퇴율
  @GetMapping("/withdraw-rate")
  @Operation(summary = "연간 탈퇴율 통계")
  public ResponseEntity<CommonApiResponse<UserStatsDto>> getWithdrawRate(@RequestParam int year) {
    return ResponseEntity.ok(CommonApiResponse.success(adminStatisticsService.getWithdrawalStats(year)));
  }

  // 매칭 통계 - 성공
  @GetMapping("/matching/success")
  @Operation(summary = "연간 매칭 성공 건수")
  public ResponseEntity<CommonApiResponse<MatchingStatsDto>> getMatchingSuccessStats(@RequestParam int year) {
    return ResponseEntity.ok(CommonApiResponse.success(adminStatisticsService.getMatchingSuccessStats(year)));
  }

  // 매칭 통계 - 실패/취소
  @GetMapping("/matching/fail")
  @Operation(summary = "연간 매칭 실패/취소 건수")
  public ResponseEntity<CommonApiResponse<MatchingStatsDto>> getMatchingFailStats(@RequestParam int year) {
    return ResponseEntity.ok(CommonApiResponse.success(adminStatisticsService.getMatchingFailStats(year)));
  }

  // 서비스 품질 (평점 평균) 통계 추후 구현 시 아래 참고
  // @GetMapping("/manager/ratings")
  // public ResponseEntity<CommonApiResponse<ManagerRatingStatsDto>> getManagerRatingStats(@RequestParam int year) {
  //   return ResponseEntity.ok(CommonApiResponse.success(adminStatisticsService.getManagerRatingStats(year)));
  // }
}
