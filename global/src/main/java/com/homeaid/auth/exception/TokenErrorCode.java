package com.homeaid.auth.exception;

import com.homeaid.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements BaseErrorCode {

  // 401 UNAUTHORIZED
  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다."),
  REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_INVALID", "리프레시 토큰 포맷이 올바르지 않거나 구조가 유효하지 않습니다."),
  REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_EXPIRED", "리프레시 토큰이 만료되었습니다."),
  REFRESH_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_MISSING", "리프레시 토큰이 존재하지 않습니다."),
  REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_NOT_FOUND", "서버에 해당 리프레시 토큰이 존재하지 않습니다."),
  REFRESH_TOKEN_REUSED(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_REUSED", "이미 사용된 리프레시 토큰입니다. 다시 로그인 해주세요."),
  TOKEN_LOGGED_OUT(HttpStatus.UNAUTHORIZED, "TOKEN_LOGGED_OUT", "이미 로그아웃된 토큰입니다."),;

  private final HttpStatus status;
  private final String code;
  private final String message;
}

