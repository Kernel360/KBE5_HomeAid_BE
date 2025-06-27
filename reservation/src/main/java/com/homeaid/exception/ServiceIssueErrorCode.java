package com.homeaid.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ServiceIssueErrorCode implements BaseErrorCode {

  // 400 BAD REQUEST

  // 403 FORBIDDEN
  UNAUTHORIZED_SERVICE_ISSUE_ACCESS(HttpStatus.FORBIDDEN, "UNAUTHORIZED_SERVICE_ISSUE_ACCESS", "서비스 이슈에 대한 접근 권한이 없습니다."),

  // 404 NOT FOUND
  SERVICE_ISSUE_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE_ISSUE_NOT_FOUND", "해당 서비스 이슈를 찾을 수 없습니다."),

  // 409 CONFLICT
  SERVICE_ISSUE_ALREADY_EXISTS(HttpStatus.CONFLICT, "SERVICE_ISSUE_ALREADY_EXISTS", "이미 해당 예약에 대한 서비스 이슈가 존재합니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

}