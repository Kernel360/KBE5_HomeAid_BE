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
    MATCHING_CREATED("고객님의 청소 요청 도착 \n지금 수락하거나 거절할 수 있습니다"),
    MATCHING_ACCEPTED_BY_CUSTOMER("예약이 확정되었습니다"),
    MATCHING_REJECTED_BY_CUSTOMER("고객이 매칭을 거절하였습니다"),
    MATCHING_ACCEPTED_BY_MANAGER("매니저가 매칭을 수락하였습니다"),
    MATCHING_ACCEPTED_BY_MANAGER_FOR_CUSTOMER("매니저님이 예약을 수락했습니다. \n결제를 완료하고 예약을 확정하세요"),
    MATCHING_REJECTED_BY_MANAGER("매니저가 매칭을 거절하였습니다"),

    // 작업 관련
    WORK_REMINDER("작업 예정 알림"),
    WORK_CHECKIN("매니저가 청소 작업을 시작했습니다"),
    WORK_CHECKOUT("매니저가 청소 작업을 마무리했습니다\n리뷰를 작성해 의견을 남겨주세요"),
    WORK_COMPLETED("매니저가 해당 예약의 작업을 완수했습니다"),

    // 시스템 관련
    PAYMENT_COMPLETED("결제가 완료되었습니다"),
    REVIEW_REQUESTED("리뷰 작성 요청");

    private final String defaultMessage;
}
