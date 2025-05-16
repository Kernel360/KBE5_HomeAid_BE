package com.example.homeaid.exception.errorcode;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    HttpStatus getStatus(); // HTTP 상태코드 반환
    String getCode(); // 프론트에 보여줄 오류 코드
    String getMessage(); // 사용자에게 보여줄 메시지
}
