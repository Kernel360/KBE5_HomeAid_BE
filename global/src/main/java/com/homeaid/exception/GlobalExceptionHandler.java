package com.homeaid.exception;


import com.homeaid.common.response.CommonApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


  @ExceptionHandler(CustomException.class)
  public ResponseEntity<CommonApiResponse<Void>> handleCustomException(CustomException e) {
    log.warn("[CustomException] {} - {}", e.getErrorCode().getCode(), e.getMessage());

    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(CommonApiResponse.fail(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
  }

  /**
   * DTO 유효성 검증 실패
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<CommonApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
    // FieldError와 ObjectError 모두 수집
    String message = e.getBindingResult().getAllErrors().stream()
        .findFirst()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .orElse("입력값이 올바르지 않습니다.");

    log.warn("[ValidationException] {}", message);

    return ResponseEntity.badRequest()
        .body(CommonApiResponse.fail("VALIDATION_ERROR", message));
  }

  /**
   * 요청 JSON 파싱 실패
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<CommonApiResponse<Void>> handleParsingException(HttpMessageNotReadableException e) {
    log.warn("[HttpMessageNotReadableException] {}", e.getMessage());

    return ResponseEntity.badRequest()
        .body(CommonApiResponse.fail("INVALID_JSON", "요청 형식이 잘못되었습니다."));
  }

  /**
   *db 제약조건에 걸릴시
   */
  @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
  public ResponseEntity<CommonApiResponse<Void>> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
    log.warn("[SQLIntegrityConstraintViolationException] {}", e.getMessage());

    return ResponseEntity.badRequest()
            .body(CommonApiResponse.fail("duplicate value", "이미 처리된 요청 입니다"));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<CommonApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
    log.error("[IllegalArgumentException] {}", e.getMessage());

    return ResponseEntity.badRequest()
        .body(CommonApiResponse.fail("INVALID_DATA_CONVERSION", "잘못된 요청입니다."));
  }

}
