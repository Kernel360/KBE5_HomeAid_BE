package com.homeaid.payment.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.payment.service.AdminRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

}
