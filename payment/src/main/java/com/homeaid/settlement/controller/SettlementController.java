package com.homeaid.settlement.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.auth.user.CustomUserDetails;
import com.homeaid.settlement.dto.response.SettlementPaymentDetailResponseDto;
import com.homeaid.settlement.service.SettlementService;
import com.homeaid.settlement.domain.Settlement;
import com.homeaid.settlement.dto.response.SettlementResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/my/settlement")
@RequiredArgsConstructor
@Tag(name = "Manager Settlement", description = "매니저 정산 조회")
public class SettlementController {

  private final SettlementService settlementService;

  @GetMapping("/weekly")
  @Operation(summary = "주간 정산 내역 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "정산 내역 조회 성공",
          content = @Content(schema = @Schema(implementation = SettlementResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "해당 정산 내역 존재하지 않음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<List<SettlementResponseDto>>> getWeeklySettlements(
      @RequestParam("start") LocalDate startDate,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {

    Long managerId = userDetails.getUserId();
    List<Settlement> settlements = settlementService.getWeeklySettlements(managerId, startDate);

    List<SettlementResponseDto> response = settlements.stream()
        .map(SettlementResponseDto::from)
        .toList();

    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  @GetMapping("/{settlementId}/payments")
  @Operation(summary = "정산 결제 상세 조회", description = "선택한 정산에 포함된 결제내역을 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = SettlementPaymentDetailResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "정산 또는 결제 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<SettlementPaymentDetailResponseDto>> getPaymentsForSettlement(
      @PathVariable Long settlementId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long managerId = userDetails.getUserId();
    SettlementPaymentDetailResponseDto response = settlementService.getPaymentsForSettlement(managerId, settlementId);

    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

}