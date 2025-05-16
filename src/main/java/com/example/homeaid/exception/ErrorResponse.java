package com.example.homeaid.exception;

import com.example.homeaid.exception.errorcode.BaseErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status; // HTTP 상태코드
    private final String code; // 상태코드 문자열
    private final String error; // 커스텀 에러코드
    private final String message; // 에러 설명 메시지

    public static ErrorResponse of(BaseErrorCode errorCode) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().name())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

}
