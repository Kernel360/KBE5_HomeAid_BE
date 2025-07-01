package com.homeaid.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements BaseErrorCode {

  // 400 BAD REQUEST
  INVALID_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "INVALID_RESERVATION_TIME", "예약 시간이 올바르지 않습니다."),
  INVALID_RESERVATION_STATUS(HttpStatus.BAD_REQUEST, "INVALID_RESERVATION_STATUS", "현재 상태에서는 예약을 수정할 수 없습니다."),
  INVALID_RESERVATION_REGION(HttpStatus.BAD_REQUEST, "INVALID_RESERVATION_REGION", "존재하지 않는 시/군/구 지역입니다."),

  // 403 FORBIDDEN
  UNAUTHORIZED_RESERVATION_ACCESS(HttpStatus.FORBIDDEN, "UNAUTHORIZED_RESERVATION_ACCESS", "예약에 대한 접근 권한이 없습니다."),
  RESERVATION_MANAGER_MISMATCH(HttpStatus.FORBIDDEN, "RESERVATION_MANAGER_MISMATCH", "예약의 매니저 아이디와 유저 아이디가 일치하지 않습니다."),
  USER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "USER_ACCESS_DENIED", "예약의 매니저(고객) 아이디와 일치하지 않습니다."),

  // 404 NOT FOUND
  RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", "예약을 찾을 수 없습니다."),
  SERVICE_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE_OPTION_NOT_FOUND", "선택한 서비스 옵션을 찾을 수 없습니다."),

  // 409 CONFLICT
  RESERVATION_CANNOT_UPDATE(HttpStatus.CONFLICT, "RESERVATION_CANNOT_UPDATE", "예약을 수정할 수 없습니다."),
  RESERVATION_ALREADY_MATCHED(HttpStatus.CONFLICT, "RESERVATION_ALREADY_MATCHED", "이미 매칭된 예약입니다."),
  RESERVATION_ALREADY_CANCELLED(HttpStatus.CONFLICT, "RESERVATION_ALREADY_CANCELLED", "이미 취소된 예약입니다."),
  RESERVATION_NOT_COMPLETED(HttpStatus.CONFLICT, "RESERVATION_NOT_COMPLETED", "서비스가 완료된 예약이 아닙니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

}