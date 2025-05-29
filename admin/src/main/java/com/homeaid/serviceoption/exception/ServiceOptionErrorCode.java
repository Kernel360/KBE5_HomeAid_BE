package com.homeaid.serviceoption.exception;

import com.homeaid.exception.BaseErrorCode;
import com.homeaid.exception.ErrorCode;
import org.springframework.http.HttpStatus;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ServiceOptionErrorCode implements BaseErrorCode {

  // 404 NOT FOUND
  OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "OPTION_NOT_FOUND", "해당 상위 옵션이 존재하지 않습니다."),
  SUB_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "SUB_OPTION_NOT_FOUND", "해당 하위 옵션이 존재하지 않습니다."),

  // 400 BAD REQUEST
  INVALID_OPTION_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_OPTION_REQUEST", "유효하지 않은 옵션 요청입니다."),
  DUPLICATE_OPTION_NAME(HttpStatus.BAD_REQUEST, "DUPLICATE_OPTION_NAME", "이미 존재하는 옵션 이름입니다.");


  private final HttpStatus status;
  private final String code;
  private final String message;
}
