package com.homeaid.domain;

import lombok.Getter;

@Getter
public enum PaymentStatus {
  PAID("결제완료"),
  CANCELED("결제취소"),
  REFUNDED("환불");

  private final String description;

  PaymentStatus(String description) {
    this.description = description;
  }
}
