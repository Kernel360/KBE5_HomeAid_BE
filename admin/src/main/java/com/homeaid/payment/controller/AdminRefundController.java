package com.homeaid.payment.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.payment.dto.RefundAdminDecisionRequestDto;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.payment.dto.response.RefundResponseDto;
import com.homeaid.payment.service.AdminRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/refunds")
@RequiredArgsConstructor
@Tag(name = "Admin Refund", description = "관리자 환불 처리 및 환불 내역 조회")
public class AdminRefundController {

  private final AdminRefundService adminRefundService;

  @PostMapping("/{paymentId}/full")
  @Operation(summary = "전액 환불 처리")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "전액 환불 성공",
          content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "결제 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<PaymentResponseDto>> refundFull(
      @PathVariable Long paymentId,
      @RequestBody @Valid RefundAdminDecisionRequestDto decisionRequest) {

    PaymentResponseDto response = adminRefundService.refundFull(paymentId, decisionRequest);
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  @PostMapping("/{paymentId}/partial")
  @Operation(summary = "부분 환불 처리(금액/비율 지원)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "부분 환불 성공",
          content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (금액/비율 오류)",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "결제 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<PaymentResponseDto>> refundPartial(
      @PathVariable Long paymentId,
      @RequestBody @Valid RefundAdminDecisionRequestDto decisionRequest) {

    PaymentResponseDto response = adminRefundService.refundPartial(paymentId, decisionRequest);
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  @PostMapping("/{refundId}/approve")
  @Operation(summary = "관리자 환불 승인")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "환불 승인 성공",
          content = @Content(schema = @Schema(implementation = RefundResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (코멘트 누락 등)",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "환불 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<RefundResponseDto>> approveRefund(
      @PathVariable Long refundId,
      @RequestBody @Valid RefundAdminDecisionRequestDto request) {

    RefundResponseDto response = adminRefundService.approveRefund(refundId, request.getAdminComment());
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  @PostMapping("/{refundId}/reject")
  @Operation(summary = "관리자 환불 거절")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "환불 거절 성공",
          content = @Content(schema = @Schema(implementation = RefundResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (코멘트 누락 등)",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "환불 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<RefundResponseDto>> rejectRefund(
      @PathVariable Long refundId,
      @RequestBody @Valid RefundAdminDecisionRequestDto request) {

    RefundResponseDto response = adminRefundService.rejectRefund(refundId, request.getAdminComment());
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  @GetMapping
  @Operation(summary = "전체 환불 내역 조회")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "환불 내역 조회 성공",
          content = @Content(schema = @Schema(implementation = RefundResponseDto.class)))
  })
  public ResponseEntity<CommonApiResponse<Page<RefundResponseDto>>> getAllRefunds(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
    Page<RefundResponseDto> refunds = adminRefundService.getAllRefunds(pageable);
    return ResponseEntity.ok(CommonApiResponse.success(refunds));
  }

  @GetMapping("/user/{userId}")
  @Operation(summary = "회원별 환불 내역 조회")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "회원 환불 내역 조회 성공",
          content = @Content(schema = @Schema(implementation = RefundResponseDto.class)))
  })
  public ResponseEntity<CommonApiResponse<Page<RefundResponseDto>>> getRefundsByUserId(
      @PathVariable Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
    Page<RefundResponseDto> refunds = adminRefundService.getRefundsByUserId(userId, pageable);
    return ResponseEntity.ok(CommonApiResponse.success(refunds));
  }

  @GetMapping("/{refundId}")
  @Operation(summary = "환불 상세 조회")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "환불 상세 조회 성공",
          content = @Content(schema = @Schema(implementation = RefundResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "환불 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<RefundResponseDto>> getRefundDetail(@PathVariable Long refundId) {
    RefundResponseDto response = adminRefundService.getRefundDetail(refundId);
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }
}
