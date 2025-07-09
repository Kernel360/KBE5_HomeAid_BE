package com.homeaid.statistics.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.exception.CustomException;
import com.homeaid.statistics.dto.AdminStatisticsDto;
import com.homeaid.statistics.dto.ManagerRatingStatsDto;
import com.homeaid.statistics.dto.MatchingStatsDto;
import com.homeaid.statistics.dto.PaymentStatsDto;
import com.homeaid.statistics.dto.ReservationStatsDto;
import com.homeaid.statistics.dto.SettlementStatsDto;
import com.homeaid.statistics.dto.UserStatsDto;
import com.homeaid.statistics.exception.StatisticsErrorCode;
import com.homeaid.statistics.service.AdminStatisticsService;
import com.homeaid.util.RedisKeyFactory;
import com.homeaid.util.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import java.time.Duration;
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

  private final RedisUtil redisUtil;
  private final AdminStatisticsService adminStatisticsService;

  // TODO : 데이터가 Null 일 경우에 대한 방어로직 추가

  // 회원 통계
  @GetMapping("/users")
  @Operation(summary = "회원 통계 (연/월/일 선택)")
  public ResponseEntity<CommonApiResponse<UserStatsDto>> getUserStats(
      @RequestParam int year,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) Integer day) {

    return ResponseEntity.ok(CommonApiResponse.success(
        adminStatisticsService.getUserStats(year, month, day)
    ));
  }

  // 정산 통계
  @GetMapping("/settlements")
  @Operation(summary = "정산 통계 (연/월/일 선택)")
  public ResponseEntity<CommonApiResponse<SettlementStatsDto>> getSettlementStats(
      @RequestParam int year,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) Integer day) {

    return ResponseEntity.ok(CommonApiResponse.success(
        adminStatisticsService.getSettlementStats(year, month, day)
    ));
  }

  // 결제 통계
  @GetMapping("/payments")
  @Operation(summary = "결제 통계 (연/월/일 선택)")
  public ResponseEntity<CommonApiResponse<PaymentStatsDto>> getPaymentStats(
      @RequestParam int year,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) Integer day) {

    return ResponseEntity.ok(CommonApiResponse.success(
        adminStatisticsService.getPaymentStats(year, month, day)
    ));
  }

  // 예약 통계
  @GetMapping("/reservations")
  @Operation(summary = "예약 통계 (연/월/일 선택)")
  public ResponseEntity<CommonApiResponse<ReservationStatsDto>> getReservationStats(
      @RequestParam int year,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) Integer day) {

    return ResponseEntity.ok(CommonApiResponse.success(
        adminStatisticsService.getReservationStats(year, month, day)
    ));
  }

  // 매칭 통계
  @GetMapping("/matching")
  @Operation(summary = "매칭 통계 (연/월/일 선택)")
  public ResponseEntity<CommonApiResponse<MatchingStatsDto>> getMatchingStats(
      @RequestParam int year,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) Integer day) {

    return ResponseEntity.ok(CommonApiResponse.success(
        adminStatisticsService.getMatchingStats(year, month, day)
    ));
  }

  // 매니저 평점 통계
  @GetMapping("/manager-ratings")
  @Operation(summary = "매니저 평점 통계 (연/월/일 선택)")
  public ResponseEntity<CommonApiResponse<ManagerRatingStatsDto>> getManagerRatingStats(
      @RequestParam int year,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) Integer day) {

    return ResponseEntity.ok(CommonApiResponse.success(
        adminStatisticsService.getManagerRatingStats(year, month, day)
    ));
  }

  @GetMapping("/all")
  @Operation(summary = "전체 통계 (연/월/일 선택)")
  public ResponseEntity<CommonApiResponse<AdminStatisticsDto>> getAllStatistics(
      @RequestParam int year,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) Integer day) {

    String key = RedisKeyFactory.buildAdminStatisticsKey(year, month, day);
    AdminStatisticsDto dto = (AdminStatisticsDto) redisUtil.getObject(key);

    if (dto == null) {
      // Redis 미존재 시 DB fallback + Redis 재저장
      dto = adminStatisticsService.getStatisticsOrLoad(year, month, day);
      redisUtil.save(key, dto, Duration.ofDays(30));
    }

    // 최종 null 체크
    if (dto == null) {
      throw new CustomException(StatisticsErrorCode.STATISTICS_NOT_FOUND);
    }

    return ResponseEntity.ok(CommonApiResponse.success(dto));
  }

}
