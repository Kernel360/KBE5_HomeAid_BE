package com.homeaid.settlement.domain.enumerate;

public enum SettlementStatus {
  PENDING,     // 예상 정산 생성됨
  APPROVED,    // 관리자가 승인함
  PAID         // 지급 완료
}
