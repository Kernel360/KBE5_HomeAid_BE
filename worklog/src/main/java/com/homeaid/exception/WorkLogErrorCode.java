package com.homeaid.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WorkLogErrorCode implements BaseErrorCode{

    OUT_OF_RANGE(HttpStatus.FORBIDDEN, "OUT_OF_RANGE", "예약 위치 범위 밖입니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", "존재하지 않는 예약입니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
