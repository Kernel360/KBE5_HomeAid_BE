package com.homeaid.payment.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.payment.dto.request.PaymentRequestDto;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.payment.dto.response.ReservationPaymentDetailResponseDto;
import com.homeaid.security.user.CustomUserDetails;
import com.homeaid.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/my/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "수요자 결제")
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping
  @Operation(summary = "수요자 결제 요청")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "결제 요청 성공",
          content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "예약 정보 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<PaymentResponseDto>> pay(@Valid @RequestBody PaymentRequestDto requestDto) {
    PaymentResponseDto response = paymentService.pay(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(CommonApiResponse.success(response));
  }

  @PostMapping("/{paymentId}/cancel")
  @Operation(summary = "수요자 결제 취소 요청")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "결제 취소 성공",
          content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "이미 환불됨",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "403", description = "본인 결제 아님",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "결제 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<PaymentResponseDto>> customerCancelPayment(
      @PathVariable Long paymentId,
      @AuthenticationPrincipal CustomUserDetails user) {

    PaymentResponseDto response = paymentService.customerCancelPayment(user.getUserId(), paymentId);
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  @GetMapping("/{paymentId}")
  @Operation(summary = "예약 + 결제 상세 조회", description = "예약 정보와 결제 정보를 통합 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = ReservationPaymentDetailResponseDto.class))),
      @ApiResponse(responseCode = "403", description = "본인 결제 아님",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "결제 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<ReservationPaymentDetailResponseDto>> getReservationPaymentDetail(
      @PathVariable Long paymentId,
      @AuthenticationPrincipal CustomUserDetails user) {

    ReservationPaymentDetailResponseDto response =
        paymentService.getReservationPaymentDetail(user.getUserId(), paymentId);

    return ResponseEntity.ok(CommonApiResponse.success(response));
  }


  @GetMapping("/list")
  public ResponseEntity<CommonApiResponse<List<PaymentResponseDto>>> getAllPayments(
      @AuthenticationPrincipal CustomUserDetails user) {
    return ResponseEntity.ok(CommonApiResponse.success(paymentService.getAllPayments(user.getUserId())));
  }

  @GetMapping("/detail/{paymentId}")
  @Operation(summary = "결제 상세 조회", description = "단일 결제 내역 상세 정보를 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
      @ApiResponse(responseCode = "403", description = "본인 결제 아님",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "결제 내역 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<PaymentResponseDto>> getPaymentDetail(
      @PathVariable Long paymentId,
      @AuthenticationPrincipal CustomUserDetails user) {

    PaymentResponseDto response = paymentService.getPaymentDetail(user.getUserId(), paymentId);
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }


}
