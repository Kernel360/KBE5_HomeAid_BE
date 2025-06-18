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
  MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "MANAGER_NOT_FOUND", "해당 매니저를 찾을 수 없습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
