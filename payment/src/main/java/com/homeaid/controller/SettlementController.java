package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Settlement;
import com.homeaid.dto.request.SettlementRequestDto;
import com.homeaid.dto.response.SettlementResponseDto;
import com.homeaid.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settlement")
@RequiredArgsConstructor
@Tag(name = "Settlement", description = "매니저 정산")
public class SettlementController {

  private final SettlementService settlementService;

  @PostMapping("/manager/{managerId}")
  @Operation(summary = "[관리자] 매니저가 정산 신청", responses = {
      @ApiResponse(responseCode = "201", description = "정산",
          content = @Content(schema = @Schema(implementation = SettlementResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "정산 실패",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<SettlementResponseDto>> calcAndSaveForManager(
      @Valid @RequestBody SettlementRequestDto dto
  ) {
    Settlement settlement = settlementService.createWeeklySettlementForManager(dto.getManagerId(), dto.getFrom(), dto.getTo());
    SettlementResponseDto responseDto = SettlementResponseDto.from(settlement);
    return ResponseEntity.status(HttpStatus.CREATED).body(CommonApiResponse.success(responseDto));
  }

  @GetMapping("/list")
  @Operation(summary = "[관리자] 정산 전체 내역 조회", responses = {
      @ApiResponse(responseCode = "200", description = "정산 전체 내역 조회 성공",
          content = @Content(schema = @Schema(implementation = SettlementResponseDto.class)))
  })
  public ResponseEntity<CommonApiResponse<List<SettlementResponseDto>>> getAllSettlements() {
    List<Settlement> settlements = settlementService.findAll();
    List<SettlementResponseDto> response = settlements.stream()
        .map(SettlementResponseDto::from)
        .toList();
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  @GetMapping("/{settlementId}")
  @Operation(summary = "[관리자] 정산 단건 조회", responses = {
      @ApiResponse(responseCode = "200", description = "정산 단건 조회 성공",
          content = @Content(schema = @Schema(implementation = SettlementResponseDto.class)))
  })
  public ResponseEntity<CommonApiResponse<SettlementResponseDto>> getSettlementById(@PathVariable Long settlementId) {
    Settlement settlement = settlementService.findById(settlementId);
    return ResponseEntity.ok(CommonApiResponse.success(SettlementResponseDto.from(settlement)));
  }

  @GetMapping("/manager/{managerId}/list")
  @Operation(summary = "[매니저] 매니저별 정산내역 전체 조회", responses = {
      @ApiResponse(responseCode = "200", description = "매니저 정산내역 전체 조회 성공",
          content = @Content(schema = @Schema(implementation = SettlementResponseDto.class)))
  })
  public ResponseEntity<CommonApiResponse<List<SettlementResponseDto>>> getSettlementsByManager(@PathVariable Long managerId) {
    List<Settlement> settlements = settlementService.findByManagerId(managerId);
    List<SettlementResponseDto> response = settlements.stream()
        .map(SettlementResponseDto::from)
        .toList();
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

}
