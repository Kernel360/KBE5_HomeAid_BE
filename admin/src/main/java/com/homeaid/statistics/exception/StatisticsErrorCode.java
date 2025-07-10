package com.homeaid.statistics.exception;

import com.homeaid.exception.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StatisticsErrorCode implements BaseErrorCode {

  STATISTICS_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STATISTICS_SERIALIZATION_FAILED", "통계 JSON 직렬화에 실패했습니다."),
  STATISTICS_DESERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STATISTICS_DESERIALIZATION_FAILED", "통계 JSON 역직렬화에 실패했습니다."),
  STATISTICS_NOT_FOUND(HttpStatus.NOT_FOUND, "STATISTICS_NOT_FOUND", "요청한 날짜의 통계 데이터가 존재하지 않습니다."),
  STATISTICS_REDIS_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "STATISTICS_REDIS_CONNECTION_FAILED", "Redis 연결에 실패했습니다."),

  // 파라미터 유효성 검사 관련 에러 추가
  INVALID_YEAR(HttpStatus.BAD_REQUEST, "INVALID_YEAR", "유효하지 않은 연도입니다."),
  INVALID_MONTH(HttpStatus.BAD_REQUEST, "INVALID_MONTH", "유효하지 않은 월입니다."),
  INVALID_DAY(HttpStatus.BAD_REQUEST, "INVALID_DAY", "유효하지 않은 일자입니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

  StatisticsErrorCode(HttpStatus status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }

}
