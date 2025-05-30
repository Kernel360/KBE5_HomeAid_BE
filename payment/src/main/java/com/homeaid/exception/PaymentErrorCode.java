package com.homeaid.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements BaseErrorCode{

  PAYMENT_RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_RESERVATION_NOT_FOUND", "예약이 존재하지 않습니다."),
  PAYMENT_INVALID_RESERVATION_STATUS(HttpStatus.BAD_REQUEST, "PAYMENT_INVALID_RESERVATION_STATUS", "예약 상태가 COMPLETED 일 때만 결제할 수 있습니다."),
  PAYMENT_ALREADY_PAID(HttpStatus.CONFLICT, "PAYMENT_ALREADY_PAID", "이미 결제가 완료된 예약입니다."),
  PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PAYMENT_AMOUNT_MISMATCH", "결제 금액이 예약 금액과 일치하지 않습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
