package com.example.homeaid.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("CustomException: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        HttpStatus status = e.getErrorCode().getStatus();
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(Exception.class) // catch-all
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e) {
        log.error("Unhandled Exception", e);

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .code("INTERNAL_SERVER_ERROR")
                .message("서버 오류가 발생했습니다. 관리자에게 문의하세요.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
