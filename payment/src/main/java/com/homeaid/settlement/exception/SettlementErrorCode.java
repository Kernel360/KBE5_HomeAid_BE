package com.homeaid.settlement.exception;

import com.homeaid.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SettlementErrorCode implements BaseErrorCode {

  NO_PAYMENTS_FOUND(HttpStatus.NOT_FOUND,"NO_PAYMENTS_FOUND", "해당 기간에 결제 내역이 없습니다."),
  ALREADY_SETTLED(HttpStatus.NOT_FOUND,"ALREADY_SETTLED", "이미 해당 기간에 정산이 완료되었습니다."),
  INVALID_REQUEST(HttpStatus.NOT_FOUND,"INVALID_REQUEST", "잘못된 정산 요청입니다."),
  MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "MANAGER_NOT_FOUND", "해당 매니저를 찾을 수 없습니다."),
  NO_SETTLEMENT_FOUND(HttpStatus.NOT_FOUND, "NO_SETTLEMENT_FOUND", "정산 내역을 찾을 수 없습니다."),

  UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "UNAUTHORIZED_ACCESS", "본인의 정산 내역만 조회할 수 없습니다."),

  ALREADY_CONFIRMED(HttpStatus.CONFLICT, "ALREADY_CONFIRMED", "이미 승인된 정산입니다."),
  ALREADY_PAID(HttpStatus.CONFLICT, "ALREADY_PAID", "이미 지급이 완료된 정산입니다."),

  NOT_CONFIRMED(HttpStatus.BAD_REQUEST, "NOT_CONFIRMED", "승인되지 않은 정산은 지급할 수 없습니다."),
  INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "INVALID_PAYMENT_STATUS", "정산할 수 없는 결제 상태입니다."),
  INVALID_RESERVATION_STATUS(HttpStatus.BAD_REQUEST, "INVALID_RESERVATION_STATUS", "정산할 수 없는 예약 상태입니다."),
  INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "INVALID_DATE_RANGE", "조회 기간의 시작일이 종료일보다 늦습니다."),

  DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB_ERROR", "정산 처리 중 데이터베이스 오류가 발생했습니다."),
  UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UNKNOWN_ERROR", "알 수 없는 오류가 발생했습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
