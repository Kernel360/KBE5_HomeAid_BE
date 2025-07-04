package com.homeaid.payment.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.payment.dto.RefundAdminDecisionRequestDto;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.payment.dto.response.RefundResponseDto;
import com.homeaid.payment.service.AdminRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/refunds")
@RequiredArgsConstructor
@Tag(name = "Admin Refund", description = "관리자 환불 처리")
public class AdminRefundController {

  private final AdminRefundService adminRefundService;

  @PostMapping("/{paymentId}/full")
  @Operation(summary = "전액 환불 처리")
  public ResponseEntity<CommonApiResponse<PaymentResponseDto>> refundFull(@PathVariable Long paymentId) {
    return ResponseEntity.ok(CommonApiResponse.success(adminRefundService.refundFull(paymentId)));
  }

  @PostMapping("/{paymentId}/partial")
  @Operation(summary = "부분 환불 처리")
  public ResponseEntity<CommonApiResponse<PaymentResponseDto>> refundPartial(
      @PathVariable Long paymentId,
      @RequestParam int refundAmount) {
    return ResponseEntity.ok(CommonApiResponse.success(adminRefundService.refundPartial(paymentId, refundAmount)));
  }

  @PostMapping("/{refundId}/approve")
  @Operation(summary = "관리자 환불 승인", description = "관리자가 환불 요청을 승인하고 승인 메시지를 입력합니다.")
  public ResponseEntity<CommonApiResponse<RefundResponseDto>> approveRefund(
      @PathVariable Long refundId,
      @RequestBody @Valid RefundAdminDecisionRequestDto request) {

    RefundResponseDto response = adminRefundService.approveRefund(refundId, request.getAdminComment());
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  @PostMapping("/{refundId}/reject")
  @Operation(summary = "관리자 환불 거절", description = "관리자가 환불 요청을 거절하고 거절 메시지를 입력합니다.")
  public ResponseEntity<CommonApiResponse<RefundResponseDto>> rejectRefund(
      @PathVariable Long refundId,
      @RequestBody @Valid RefundAdminDecisionRequestDto request) {

    RefundResponseDto response = adminRefundService.rejectRefund(refundId, request.getAdminComment());
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

}
