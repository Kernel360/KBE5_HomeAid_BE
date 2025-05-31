package com.homeaid.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements BaseErrorCode {

  // 400 BAD_REQUEST
  INVALID_BOARD_ID(HttpStatus.BAD_REQUEST, "INVALID_BOARD_ID", "유효하지 않은 게시글 ID입니다."),

  // 401 UNAUTHORIZED
  BOARD_ACCESS_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "BOARD_ACCESS_UNAUTHORIZED",
      "게시글에 대한 접근 권한이 없습니다."),

  // 403 FORBIDDEN
  BOARD_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "BOARD_UPDATE_FORBIDDEN", "게시글 수정 권한이 없습니다."),
  BOARD_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "BOARD_DELETE_FORBIDDEN", "게시글 삭제 권한이 없습니다."),
  BOARD_VIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "BOARD_VIEW_FORBIDDEN", "게시글 조회 권한이 없습니다."),
  BOARD_ANSWERED_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "BOARD_ANSWERED_UPDATE_FORBIDDEN",
      "답변이 완료된 게시글은 수정할 수 없습니다."),

  // 404 NOT_FOUND
  BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD_NOT_FOUND", "문의글이 존재하지 않습니다."),

  // 500 INTERNAL_SERVER_ERROR
  BOARD_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BOARD_SAVE_FAILED", "게시글 저장에 실패했습니다."),
  BOARD_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BOARD_UPDATE_FAILED", "게시글 수정에 실패했습니다."),
  BOARD_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BOARD_DELETE_FAILED", "게시글 삭제에 실패했습니다."),
  BOARD_SEARCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BOARD_SEARCH_FAILED", "게시글 검색에 실패했습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}