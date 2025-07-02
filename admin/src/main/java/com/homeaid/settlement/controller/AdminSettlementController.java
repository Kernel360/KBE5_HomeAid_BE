package com.homeaid.settlement.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.settlement.domain.Settlement;
import com.homeaid.settlement.dto.SettlementWithManagerResponseDto;
import com.homeaid.settlement.dto.response.SettlementResponseDto;
import com.homeaid.settlement.service.AdminSettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/settlements")
@Tag(name = "AdminSettlement", description = "관리자 정산 API")
public class AdminSettlementController {

  private final AdminSettlementService adminSettlementService;

  @PostMapping("/manager")
  @Operation(summary = "[관리자] 매니저 주간 정산 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "매니저 주간 정산 생성 성공",
          content = @Content(schema = @Schema(implementation = SettlementResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "매니저를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "400", description = "결제 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<SettlementResponseDto>> createManagerSettlement(
      @RequestParam Long managerId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekEnd
  ) {
    Settlement settlement = adminSettlementService.createWeeklySettlementForManager(managerId, weekStart, weekEnd);
    return ResponseEntity.ok(CommonApiResponse.success(SettlementResponseDto.from(settlement)));
  }

  @GetMapping
  @Operation(summary = "[관리자] 전체 정산 내역 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "전체 정산 내역 조회 성공",
          content = @Content(schema = @Schema(implementation = SettlementResponseDto.class)))
  })
  public ResponseEntity<CommonApiResponse<List<SettlementResponseDto>>> getAllSettlements(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
      @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
      @RequestParam(value = "page", defaultValue = "0") int page,
      @Parameter(description = "페이지 크기", example = "10")
      @RequestParam(value = "size", defaultValue = "10") int size) {

    List<Settlement> settlements = adminSettlementService.findAll(status, start, end);
    List<SettlementResponseDto> response = settlements.stream()
        .map(SettlementResponseDto::from)
        .toList();

    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  @GetMapping("/managers/{managerId}")
  @Operation(summary = "[관리자] 매니저별 정산 내역 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "매니저 정산 내역 조회 성공",
          content = @Content(schema = @Schema(implementation = SettlementResponseDto.class)))
  })
  public ResponseEntity<CommonApiResponse<List<SettlementResponseDto>>> getSettlementsByManager(
      @PathVariable Long managerId,
      @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
      @RequestParam(value = "page", defaultValue = "0") int page,
      @Parameter(description = "페이지 크기", example = "10")
      @RequestParam(value = "size", defaultValue = "10") int size) {

    List<Settlement> settlements = adminSettlementService.findByManagerId(managerId);
    List<SettlementResponseDto> response = settlements.stream()
        .map(SettlementResponseDto::from)
        .toList();

    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  @GetMapping("/{settlementId}")
  @Operation(summary = "[관리자] 단건 정산 상세 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "정산 단건 조회 성공",
          content = @Content(schema = @Schema(implementation = SettlementResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "정산 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<SettlementResponseDto>> getSettlementById(
      @PathVariable Long settlementId) {

    Settlement settlement = adminSettlementService.findById(settlementId);
    return ResponseEntity.ok(CommonApiResponse.success(SettlementResponseDto.from(settlement)));
  }

  @PostMapping("/{settlementId}/confirm")
  @Operation(summary = "[관리자] 정산 승인 처리")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "정산 승인 성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "400", description = "이미 승인됨",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<SettlementResponseDto>> confirmSettlement(@PathVariable Long settlementId) {
    adminSettlementService.confirm(settlementId);
    Settlement updated = adminSettlementService.findById(settlementId);
    return ResponseEntity.ok(CommonApiResponse.success(SettlementResponseDto.from(updated)));
  }

  @PostMapping("/{settlementId}/pay")
  @Operation(summary = "[관리자] 정산 지급 처리")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "정산 지급 성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "400", description = "미승인 상태거나 이미 지급됨",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<SettlementResponseDto>> paySettlement(@PathVariable Long settlementId) {
    adminSettlementService.pay(settlementId);
    Settlement updated = adminSettlementService.findById(settlementId);
    return ResponseEntity.ok(CommonApiResponse.success(SettlementResponseDto.from(updated)));
  }

  @GetMapping("/{settlementId}/manager-detail")
  @Operation(summary = "[관리자] 매니저 정산 상세조회", description = "정산 정보와 매니저 상세 정보를 함께 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "매니저 정산 상세조회 성공",
          content = @Content(schema = @Schema(implementation = SettlementWithManagerResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "정산 정보 또는 매니저 정보 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<SettlementWithManagerResponseDto>> getSettlementWithManager(
      @PathVariable Long settlementId) {
    SettlementWithManagerResponseDto dto = adminSettlementService.getSettlementWithManager(settlementId);
    return ResponseEntity.ok(CommonApiResponse.success(dto));
  }


}