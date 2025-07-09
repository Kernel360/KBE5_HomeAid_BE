package com.homeaid.payment.domain.enumerate;

public enum RefundReason {
  BEFORE_7_DAYS,            // 예약 7일 전 이상: 100% 환불 가능
  BETWEEN_3_AND_7_DAYS,     // 예약 3~7일 전: 최대 50% 환불
  LESS_THAN_3_DAYS,         // 예약 72시간 미만: 최대 30% 환불
  AFTER_COMPLETION,         // 서비스 완료 후 3일 이내 부분 환불 요청
  CUSTOMER_DISSATISFACTION, // 서비스 불만족 환불
  CUSTOMER_REQUEST,         // 고객이 환불을 요청했을 때
  ADMIN_MANUAL_REFUND       // 관리자가 수동으로 환불했을 때
}
