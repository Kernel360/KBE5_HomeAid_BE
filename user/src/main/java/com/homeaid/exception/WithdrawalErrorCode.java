package com.homeaid.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WithdrawalErrorCode implements BaseErrorCode {
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "WITHDRAWAL_USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다."),
  REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "WITHDRAWAL_REQUEST_NOT_FOUND", "탈퇴 요청을 찾을 수 없습니다."),
  ALREADY_REQUESTED(HttpStatus.CONFLICT, "ALREADY_REQUESTED", "이미 탈퇴 요청을 보낸 회원입니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
