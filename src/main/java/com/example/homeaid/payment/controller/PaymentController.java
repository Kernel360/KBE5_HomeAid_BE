package com.example.homeaid.payment.controller;

import com.example.homeaid.customer.customerboard.dto.response.UpdateBoardResponseDto;
import com.example.homeaid.global.common.response.CommonApiResponse;
import com.example.homeaid.payment.dto.PaymentRequestDto;
import com.example.homeaid.payment.dto.PaymentResponseDto;
import com.example.homeaid.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping
  @Operation(summary = "결제 성공", responses = {
      @ApiResponse(responseCode = "201", description = "결제 성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<PaymentResponseDto> pay(@Valid @RequestBody PaymentRequestDto requestDto) {
    PaymentResponseDto response = paymentService.pay(requestDto);
    return ResponseEntity.ok(response);
  }

}
