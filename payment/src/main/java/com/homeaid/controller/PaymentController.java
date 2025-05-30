package com.homeaid.controller;

import com.homeaid.domain.Payment;
import com.homeaid.dto.request.PaymentRequestDto;
import com.homeaid.dto.response.PaymentResponseDto;
import com.homeaid.repository.PaymentRepository;
import com.homeaid.service.PaymentService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "payments", description = "결제 관련 API")
public class PaymentController {

  private final PaymentService paymentService;
  private final PaymentRepository paymentRepository;

  @PostMapping
  @Operation(summary = "결제 성공", responses = {
      @ApiResponse(responseCode = "201", description = "결제 성공",
          content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
  })
  public ResponseEntity<PaymentResponseDto> pay(@Valid @RequestBody PaymentRequestDto requestDto) {
    PaymentResponseDto response = paymentService.pay(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

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
