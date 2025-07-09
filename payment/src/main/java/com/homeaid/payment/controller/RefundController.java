package com.homeaid.payment.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.payment.dto.request.RefundRequestDto;
import com.homeaid.payment.dto.response.RefundResponseDto;
import com.homeaid.payment.service.RefundService;
import com.homeaid.security.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/my/refunds")
@RequiredArgsConstructor
@Tag(name = "Refund", description = "수요자 환불 요청")
public class RefundController {

  private final RefundService refundService;

  @PostMapping
  @Operation(summary = "환불 요청")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "환불 요청 성공",
          content = @Content(schema = @Schema(implementation = RefundResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (필드 누락, 정책 위반 등)",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "403", description = "본인 결제 아님",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "결제 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<RefundResponseDto>> requestRefund(
      @RequestBody RefundRequestDto requestDto,
      @AuthenticationPrincipal CustomUserDetails user) {

    RefundResponseDto response = refundService.requestRefund(user.getUserId(), requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(CommonApiResponse.success(response));
  }

  @GetMapping
  @Operation(summary = "내 환불 내역 전체 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "환불 내역 전체 조회 성공",
          content = @Content(schema = @Schema(implementation = RefundResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "로그인 필요",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<List<RefundResponseDto>>> getMyRefunds(
      @AuthenticationPrincipal CustomUserDetails user) {

    List<RefundResponseDto> refunds = refundService.getMyRefunds(user.getUserId());
    return ResponseEntity.ok(CommonApiResponse.success(refunds));
  }

  @GetMapping("/{refundId}")
  @Operation(summary = "내 환불 상세 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "환불 상세 조회 성공",
          content = @Content(schema = @Schema(implementation = RefundResponseDto.class))),
      @ApiResponse(responseCode = "403", description = "본인 환불 아님",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "환불 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<RefundResponseDto>> getMyRefundDetail(
      @PathVariable Long refundId,
      @AuthenticationPrincipal CustomUserDetails user) {

    RefundResponseDto refund = refundService.getMyRefundDetail(user.getUserId(), refundId);
    return ResponseEntity.ok(CommonApiResponse.success(refund));
  }

  @PostMapping("/{refundId}/cancel")
  @Operation(summary = "환불 요청 철회", description = "고객이 자신의 환불 요청을 철회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "환불 요청 철회 성공",
          content = @Content(schema = @Schema(implementation = RefundResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "취소 불가능한 상태",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "403", description = "본인 환불 아님",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "환불 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<RefundResponseDto>> cancelRefund(
      @PathVariable Long refundId,
      @AuthenticationPrincipal CustomUserDetails user) {

    RefundResponseDto response = refundService.cancelRefundRequest(user.getUserId(), refundId);
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

}
