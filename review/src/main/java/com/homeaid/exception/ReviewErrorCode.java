package com.homeaid.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements BaseErrorCode {

    // 400 BAD REQUEST
    INVALID_REVIEW_CONTENT(HttpStatus.BAD_REQUEST, "INVALID_REVIEW_CONTENT", "리뷰 내용이 올바르지 않습니다."),
    INVALID_REVIEW_SCORE(HttpStatus.BAD_REQUEST, "INVALID_REVIEW_SCORE", "리뷰 점수가 유효하지 않습니다."),

    // 403 FORBIDDEN
    UNAUTHORIZED_REVIEW_ACCESS(HttpStatus.FORBIDDEN, "UNAUTHORIZED_REVIEW_ACCESS", "리뷰에 대한 권한이 없습니다."),

    // 409 CONFLICT
    REVIEW_NOT_ALLOWED(HttpStatus.CONFLICT, "REVIEW_NOT_ALLOWED", "작업이 완료되지 않아 리뷰를 작성할 수 없습니다."),
    DUPLICATE_REVIEW(HttpStatus.CONFLICT, "DUPLICATE_REVIEW", "이미 해당 예약에 대한 리뷰가 존재합니다."),
    REVIEW_CANNOT_UPDATE(HttpStatus.CONFLICT, "REVIEW_CANNOT_UPDATE", "리뷰를 수정할 수 없습니다."),
    REVIEW_CANNOT_DELETE(HttpStatus.CONFLICT, "REVIEW_CANNOT_DELETE", "리뷰를 삭제할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
