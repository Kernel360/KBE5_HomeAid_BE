package com.homeaid.matching.exception;


import com.homeaid.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MatchingErrorCode implements BaseErrorCode {

  // 400 BAD REQUEST
  INVALID_MATCHING_ACTION(HttpStatus.BAD_REQUEST, "INVALID_MATCHING_ACTION", "유효하지 않은 매칭 요청입니다."),
  MEMO_REQUIRED_FOR_REJECTION(HttpStatus.BAD_REQUEST, "MEMO_REQUIRED_FOR_REJECTION", "거절 시에는 메모를 작성해야 합니다."),
  MATCHING_ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST, "MATCHING_ALREADY_CONFIRMED", "이미 확정된 매칭입니다."),
  MATCHING_ALREADY_REJECTED(HttpStatus.BAD_REQUEST, "MATCHING_ALREADY_REJECTED", "이미 거절된 매칭입니다."),
  MATCHING_ALREADY_ACCEPTED(HttpStatus.BAD_REQUEST, "MATCHING_ALREADY_ACCEPTED", "이미 수락된 매칭입니다."),

  // 404 NOT FOUND
  MATCHING_NOT_FOUND(HttpStatus.NOT_FOUND, "MATCHING_NOT_FOUND", "매칭 정보를 찾을 수 없습니다."),

  // 403 FORBIDDEN
  UNAUTHORIZED_MATCHING_ACCESS(HttpStatus.FORBIDDEN, "UNAUTHORIZED_MATCHING_ACCESS", "이 매칭에 접근할 권한이 없습니다."),
  MANAGER_MISMATCH(HttpStatus.FORBIDDEN, "MANAGER_MISMATCH", "매칭된 매니저가 일치하지 않습니다."),
  CUSTOMER_MISMATCH(HttpStatus.FORBIDDEN, "CUSTOMER_MISMATCH", "매칭된 고객이 일치하지 않습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}