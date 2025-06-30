package com.homeaid.domain.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationEventType {
    // 예약 관련
    RESERVATION_CREATED("예약이 신청되었습니다"),
    RESERVATION_CANCELLED("예약이 취소되었습니다"),

    // 매칭 관련
    MATCHING_CREATED("새로운 매칭이 생성되었습니다"),
    MATCHING_ACCEPTED_BY_CUSTOMER("고객이 매칭을 최종수락했습니다"),
    MATCHING_REJECTED_BY_CUSTOMER("고객이 매칭을 거절하였습니다"),
    MATCHING_ACCEPTED_BY_MANAGER("매니저가 매칭을 수락하였습니다"),
    MATCHING_REJECTED_BY_MANAGER("매니저가 매칭을 거절하였습니다"),

    // 작업 관련
    WORK_REMINDER("작업 예정 알림"),
    WORK_CHECKIN("체크인 알림"),
    WORK_CHECKOUT("체크아웃 알림"),
    WORK_COMPLETED("작업 완료 알림"),

    // 시스템 관련
    PAYMENT_COMPLETED("결제가 완료되었습니다"),
    REVIEW_REQUESTED("리뷰 작성 요청");

    private final String defaultMessage;
}
