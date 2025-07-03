package com.homeaid.payment.domain.enumerate;

import lombok.Getter;

@Getter
public enum PaymentStatus {
  PAID,             // 결제 완료
  CANCELED,         // 결제 취소
  PARTIAL_REFUNDED, // 부분환불
  REFUNDED          // 전체환불
}
