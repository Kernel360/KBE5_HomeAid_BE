package com.homeaid.payment.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.payment.service.AdminPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/payments")
@RequiredArgsConstructor
@Tag(name = "Admin Payment", description = "관리자 결제 조회")
public class AdminPaymentController {

  private final AdminPaymentService adminPaymentService;

  @GetMapping("/{paymentId}")
  @Operation(summary = "관리자 결제 단건 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "결제 단건 조회 성공",
          content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "결제 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<PaymentResponseDto>> getPayment(@PathVariable Long paymentId) {
    return ResponseEntity.ok(CommonApiResponse.success(adminPaymentService.getPayment(paymentId)));
  }

  @GetMapping("/list")
  @Operation(summary = "관리자 전체 결제 내역 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "전체 결제 내역 조회 성공",
          content = @Content(schema = @Schema(implementation = PaymentResponseDto.class)))
  })
  public ResponseEntity<CommonApiResponse<List<PaymentResponseDto>>> getAllPayments() {
    return ResponseEntity.ok(CommonApiResponse.success(adminPaymentService.getAllPayments()));
  }

  @GetMapping("/customers/{customerId}")
  @Operation(summary = "관리자 수요자별 결제 내역 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수요자별 결제 내역 조회 성공",
          content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "수요자 결제 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<List<PaymentResponseDto>>> getPaymentsByCustomer(
      @PathVariable Long customerId) {
    return ResponseEntity.ok(CommonApiResponse.success(adminPaymentService.getAllPaymentsByCustomer(customerId)));
  }

}
