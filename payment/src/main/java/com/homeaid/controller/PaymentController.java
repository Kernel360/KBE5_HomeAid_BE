package com.homeaid.controller;

import com.homeaid.domain.Payment;
import com.homeaid.dto.response.PaymentResponseDto;
import com.homeaid.repository.PaymentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "payments", description = "결제 관련 API")
public class PaymentController {


  private final PaymentRepository paymentRepository;


  @GetMapping("/list")
  @Operation(summary = "결제 내역 전체 조회", responses = {
      @ApiResponse(responseCode = "200", description = "결제 내역 조회 성공",
          content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
  })
  public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
    List<Payment> payments = paymentRepository.findAll();
    List<PaymentResponseDto> response = payments.stream()
        .map(PaymentResponseDto::toDto)
        .toList();
    return ResponseEntity.ok(response);
  }
}
