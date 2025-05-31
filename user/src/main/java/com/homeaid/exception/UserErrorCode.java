package com.homeaid.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

  // 401 UNAUTHORIZED
  USER_NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "USER_NOT_AUTHENTICATED", "인증되지 않은 사용자입니다."),


  // 409 CONFLICT
  USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_ALREADY_EXISTS", "이미 가입된 회원입니다.");

  private final HttpStatus Status;
  private final String code;
  private final String message;
}
