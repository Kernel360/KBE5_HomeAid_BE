package com.homeaid.payment.exception;

import com.homeaid.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RefundErrorCode implements BaseErrorCode {

  DUPLICATE_REFUND_REQUEST(HttpStatus.CONFLICT, "DUPLICATE_REFUND_REQUEST", "이미 환불 요청이 진행 중인 결제입니다."),

  REFUND_NOT_FOUND(HttpStatus.NOT_FOUND, "REFUND_NOT_FOUND","환불 내역을 찾을 수 없습니다."),

  REFUND_POLICY_VIOLATION(HttpStatus.BAD_REQUEST, "REFUND_POLICY_VIOLATION", "환불 정책에 맞지 않습니다."),
  REFUND_REQUEST_PERIOD_EXCEEDED(HttpStatus.BAD_REQUEST, "REFUND_REQUEST_PERIOD_EXCEEDED", "환불 요청 가능 기간이 지났습니다."),
  INVALID_REFUND_REASON(HttpStatus.BAD_REQUEST, "INVALID_REFUND_REASON", "잘못된 환불 사유입니다."),
  INVALID_REFUND_STATUS(HttpStatus.BAD_REQUEST, "INVALID_REFUND_STATUS", "환불 요청 상태가 아니어서 승인/거절할 수 없습니다."),
  CANNOT_APPROVE_REFUND(HttpStatus.BAD_REQUEST, "CANNOT_APPROVE_REFUND", "요청 상태가 아니어서 환불을 승인할 수 없습니다."),
  CANNOT_REJECT_REFUND(HttpStatus.BAD_REQUEST, "CANNOT_REJECT_REFUND", "요청 상태가 아니어서 환불을 거절할 수 없습니다."),
  CANNOT_CANCEL_REFUND(HttpStatus.BAD_REQUEST,"CANNOT_CANCEL_REFUND","요청 상태가 아니어서 환불 요청을 철회할 수 없습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

}
