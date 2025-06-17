package com.homeaid.domain;

import lombok.Getter;

@Getter
public enum PaymentMethod {
  CARD,     // 카드
  TRANSFER, // 계좌이체
  CASH;     // 현금
}
