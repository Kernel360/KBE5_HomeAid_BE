package com.homeaid.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements BaseErrorCode{

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND", "해당 알림을 찾을 수 없습니다"),
    MESSAGE_CONVERT_JSON_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "MESSAGE_CONVERT_JSON_FAIL", "redis message json 변환 실패");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
