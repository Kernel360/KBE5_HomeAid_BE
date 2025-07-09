package com.homeaid.payment.domain.enumerate;

public enum RefundStatus {
  REQUESTED,  // 고객이 환불 요청함
  CANCELLED,  // 고객이 환불 요청을 철회함
  APPROVED,   // 관리자가 승인
  REJECTED,   // 관리자가 거절
  COMPLETED   // PG사 등에서 환불 처리까지 완료
}
