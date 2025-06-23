package com.homeaid.statistics.controller;

import com.homeaid.common.response.CommonApiResponse;
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

  // TODO : 데이터가 Null 일 경우에 대한 방어로직 추가

  // 회원 통계
  @GetMapping("/users")
  @Operation(summary = "회원 통계 (연간 또는 월간)")
  public ResponseEntity<CommonApiResponse<UserStatsDto>> getUserStatistics(
      @RequestParam int year,
      @RequestParam(required = false) Integer month) {

    UserStatsDto dto = (month == null)
        ? adminStatisticsService.getUserStats(year)
        : adminStatisticsService.getUserStatsByMonth(year, month);

    return ResponseEntity.ok(CommonApiResponse.success(dto));
  }

  // 탈퇴율 통계
  @GetMapping("/withdraw-rate")
  @Operation(summary = "탈퇴율 통계 (연간 또는 월간)")
  public ResponseEntity<CommonApiResponse<UserStatsDto>> getWithdrawRate(
      @RequestParam int year,
      @RequestParam(required = false) Integer month) {

    UserStatsDto dto = (month == null)
        ? adminStatisticsService.getWithdrawalStats(year)
        : adminStatisticsService.getWithdrawalStatsByMonth(year, month);

    return ResponseEntity.ok(CommonApiResponse.success(dto));
  }

  // 정산 통계
  @GetMapping("/settlements")
  @Operation(summary = "정산 통계 (연간 또는 월간)")
  public ResponseEntity<CommonApiResponse<SettlementStatsDto>> getSettlementStatistics(
      @RequestParam int year,
      @RequestParam(required = false) Integer month) {

    SettlementStatsDto dto = (month == null)
        ? adminStatisticsService.getSettlementStats(year)
        : adminStatisticsService.getSettlementStatsByMonth(year, month);

    return ResponseEntity.ok(CommonApiResponse.success(dto));
  }

  /// 결제 통계 (연간 총액 / 월간 총액 + 수단별)
  @GetMapping("/payments")
  @Operation(summary = "결제 통계")
  public ResponseEntity<CommonApiResponse<PaymentStatsDto>> getPaymentStats(
      @RequestParam int year,
      @RequestParam(required = false) Integer month) {

    PaymentStatsDto result = (month == null)
        ? adminStatisticsService.getPaymentStats(year)
        : adminStatisticsService.getPaymentStatsByMonth(year, month);

    return ResponseEntity.ok(CommonApiResponse.success(result));
  }

  // 예약 통계
  @GetMapping("/reservations")
  @Operation(summary = "예약 통계 (연간 또는 월간)")
  public ResponseEntity<CommonApiResponse<ReservationStatsDto>> getReservationStatistics(
      @RequestParam int year,
      @RequestParam(required = false) Integer month) {

    return ResponseEntity.ok(CommonApiResponse.success(
        adminStatisticsService.getReservationStats(year, month)
    ));
  }

  // 매칭 성공 통계
  @GetMapping("/matching/success")
  @Operation(summary = "매칭 성공 통계 (연간 또는 월간)")
  public ResponseEntity<CommonApiResponse<MatchingStatsDto>> getMatchingSuccessStats(
      @RequestParam int year,
      @RequestParam(required = false) Integer month) {

    return ResponseEntity.ok(CommonApiResponse.success(
        adminStatisticsService.getMatchingSuccessStats(year, month)
    ));
  }

  // 매칭 실패/취소 통계
  @GetMapping("/matching/fail")
  @Operation(summary = "매칭 실패/취소 통계 (연간 또는 월간)")
  public ResponseEntity<CommonApiResponse<MatchingStatsDto>> getMatchingFailStats(
      @RequestParam int year,
      @RequestParam(required = false) Integer month) {

    return ResponseEntity.ok(CommonApiResponse.success(
        adminStatisticsService.getMatchingFailStats(year, month)
    ));
  }

  // 서비스 품질 (평점 평균) 통계 추후 구현 시 아래 참고
  // @GetMapping("/manager/ratings")
  // public ResponseEntity<CommonApiResponse<ManagerRatingStatsDto>> getManagerRatingStats(@RequestParam int year) {
  //   return ResponseEntity.ok(CommonApiResponse.success(adminStatisticsService.getManagerRatingStats(year)));
  // }
}
