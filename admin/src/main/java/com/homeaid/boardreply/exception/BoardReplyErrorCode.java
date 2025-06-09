package com.homeaid.boardreply.exception;

import com.homeaid.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardReplyErrorCode implements BaseErrorCode {
  INVALID_REPLY_ID(HttpStatus.BAD_REQUEST, "INVALID_REPLY_ID", "유효하지 않은 답변 ID 입니다."),
  REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND_REPLY_ID", "답변이 존재하지 않습니다."),
  REPLY_ACCESS_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "REPLY_ACCESS_UNAUTHORIZED", "답변에 대한 접근 권한이 없습니다.");


  private final HttpStatus status;
  private final String code;
  private final String message;
}
