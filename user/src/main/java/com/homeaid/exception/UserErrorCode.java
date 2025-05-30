package com.homeaid.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

  // 400 BAD REQUEST
  INVALID_USER_INPUT(HttpStatus.BAD_REQUEST, "INVALID_USER_INPUT", "잘못된 사용자 입력입니다."),
  INVALID_ROLE(HttpStatus.BAD_REQUEST, "INVALID_ROLE", "유효하지 않은 사용자 역할입니다."),
  INVALID_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "INVALID_OAUTH_PROVIDER", "지원하지 않는 소셜 로그인 제공자입니다."),

  // 401 UNAUTHORIZED
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다."),
  INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
  TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "토큰이 만료되었습니다."),

  // 403 FORBIDDEN
  FORBIDDEN_USER_ACCESS(HttpStatus.FORBIDDEN, "FORBIDDEN_USER_ACCESS", "접근 권한이 없습니다."),

  // 404 NOT FOUND
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
  MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "MANAGER_NOT_FOUND", "매니저를 찾을 수 없습니다."),
  CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "CUSTOMER_NOT_FOUND", "고객을 찾을 수 없습니다."),

  // 409 CONFLICT
  USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_ALREADY_EXISTS", "이미 가입된 회원입니다."),
  SOCIAL_ACCOUNT_ALREADY_LINKED(HttpStatus.CONFLICT, "SOCIAL_ACCOUNT_ALREADY_LINKED", "이미 연동된 소셜 계정입니다."),
  PHONE_NUMBER_ALREADY_USED(HttpStatus.CONFLICT, "PHONE_NUMBER_ALREADY_USED", "이미 사용 중인 전화번호입니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}

