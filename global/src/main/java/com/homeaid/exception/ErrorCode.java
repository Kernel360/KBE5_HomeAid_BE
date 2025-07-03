package com.homeaid.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {

  // 400 BAD REQUEST
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
  VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "요청 값이 올바르지 않습니다."),
  FILE_EMPTY(HttpStatus.BAD_REQUEST, "FILE_EMPTY", "파일이 비어있습니다."),
  INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "INVALID_IMAGE_TYPE", "유효하지 않은 이미지 형식입니다."),
  INVALID_DOCUMENT_TYPE(HttpStatus.BAD_REQUEST, "INVALID_DOCUMENT_TYPE", "지원하지 않는 파일 형식입니다. PDF, DOC, DOCX만 업로드 가능합니다."),


  // 401 UNAUTHORIZED
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다."),

  // 403 FORBIDDEN
  FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다."),

  // 404 NOT FOUND
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),

  // 405 METHOD NOT ALLOWED
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "허용되지 않은 HTTP 메서드입니다."),

  // 409 CONFLICT
  DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", "중복된 리소스입니다."),

  // 500 INTERNAL SERVER ERROR
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
      "서버 오류가 발생했습니다."),
  FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_UPLOAD_ERROR", "파일 업로드 중 에러가 발생했습니다."),
  FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_DELETE_ERROR", "파일 삭제 중 에러가 발생했습니다."),
  FILE_DOWNLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_DOWNLOAD_ERROR", "파일 다운로드 중 에러가 발생했습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

}
