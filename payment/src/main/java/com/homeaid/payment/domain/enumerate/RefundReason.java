package com.homeaid.payment.domain.enumerate;

public enum RefundReason {
  BEFORE_7_DAYS,           // 예약 7일 전 이상: 100% 환불 가능
  BETWEEN_3_AND_7_DAYS,    // 예약 3~7일 전: 최대 50% 환불
  LESS_THAN_3_DAYS,        // 예약 72시간 미만: 최대 30% 환불
  AFTER_COMPLETION,        // 서비스 완료 후 3일 이내 부분 환불 요청
  CUSTOMER_DISSATISFACTION // 서비스 불만족 환불
}
