package com.homeaid.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WorkLogErrorCode implements BaseErrorCode {

    // 400 BAD REQUEST
    INVALID_WORKLOG_TIME(HttpStatus.BAD_REQUEST, "INVALID_WORKLOG_TIME", "작업 시간이 올바르지 않습니다."),
    INVALID_WORKLOG_STATUS(HttpStatus.BAD_REQUEST, "INVALID_WORKLOG_STATUS", "작업 상태가 유효하지 않습니다."),
    ALREADY_COMPLETED_CHECKIN(HttpStatus.BAD_REQUEST, "ALREADY_COMPLETED_WORKLOG", "이미 완료된 체크인입니다."),
    ALREADY_COMPLETED_CHECKOUT(HttpStatus.BAD_REQUEST, "ALREADY_COMPLETED_WORKLOG", "이미 완료된 체크아웃입니다."),
    INVALID_WORK_DATE(HttpStatus.BAD_REQUEST, "INVALID_WORK_DATE_TIME", "체크인은 작업시작 10분 전 후로 가능합니다."),

    // 403 FORBIDDEN
    CHECKOUT_MANAGER_MISMATCH(HttpStatus.FORBIDDEN, "INVALID_WORKLOG_WORKER", "체크인한 매니저와 체크아웃을 시도한 매니저가 일치하지 않습니다."),
    OUT_OF_WORK_RANGE(HttpStatus.FORBIDDEN, "OUT_OF_WORK_RANGE", "현재 위치가 허용된 작업 범위를 벗어났습니다."),

    // 404 NOT FOUND
    WORKLOG_NOT_FOUND(HttpStatus.NOT_FOUND, "WORKLOG_NOT_FOUND", "작업 로그를 찾을 수 없습니다."),

    // 409 CONFLICT
    WORKLOG_ALREADY_EXISTS(HttpStatus.CONFLICT, "WORKLOG_ALREADY_EXISTS", "이미 해당 예약에 대한 작업 로그가 존재합니다."),
    WORKLOG_CANNOT_UPDATE(HttpStatus.CONFLICT, "WORKLOG_CANNOT_UPDATE", "작업 로그를 수정할 수 없습니다."),
    WORKLOG_CANNOT_DELETE(HttpStatus.CONFLICT, "WORKLOG_CANNOT_DELETE", "작업 로그를 삭제할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
