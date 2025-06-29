package com.homeaid.payment.exception;

import com.homeaid.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements BaseErrorCode {

  PAYMENT_RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_RESERVATION_NOT_FOUND", "예약이 존재하지 않습니다."),
  PAYMENT_INVALID_RESERVATION_STATUS(HttpStatus.BAD_REQUEST, "PAYMENT_INVALID_RESERVATION_STATUS", "예약 상태가 MATCHED 일 때만 결제할 수 있습니다."),
  PAYMENT_ALREADY_PAID(HttpStatus.CONFLICT, "PAYMENT_ALREADY_PAID", "이미 결제가 완료된 예약입니다."),
  PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PAYMENT_AMOUNT_MISMATCH", "결제 금액이 예약 금액과 일치하지 않습니다."),
  PAYMENT_INVALID_STATUS_FOR_PAYMENT(HttpStatus.BAD_REQUEST, "PAYMENT_INVALID_STATUS_FOR_PAYMENT", "결제할 수 없는 예약 상태입니다."),

  PAYMENT_CANCELLATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "PAYMENT_CANCELLATION_NOT_ALLOWED", "현재 예약 상태에서는 결제 취소가 불가능합니다."),
  PAYMENT_REFUND_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "PAYMENT_REFUND_NOT_ALLOWED", "현재 예약 상태에서는 환불이 불가능합니다."),

  PAYMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PAYMENT_ACCESS_DENIED", "본인의 결제가 아닙니다."),

  PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_NOT_FOUND", "결제 내역을 찾을 수 없습니다."),
  PAYMENT_INVALID_STATUS(HttpStatus.BAD_REQUEST, "PAYMENT_INVALID_STATUS", "유효하지 않은 결제 상태입니다."),
  PAYMENT_ALREADY_REFUNDED(HttpStatus.CONFLICT, "PAYMENT_ALREADY_REFUNDED", "이미 환불이 완료된 결제입니다."),
  REFUND_AMOUNT_EXCEEDS_PAYMENT(HttpStatus.BAD_REQUEST, "REFUND_AMOUNT_EXCEEDS_PAYMENT", "환불 금액이 결제 금액을 초과합니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
