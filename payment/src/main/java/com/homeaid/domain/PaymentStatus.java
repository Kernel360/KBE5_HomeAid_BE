package com.homeaid.domain;

import lombok.Getter;

@Getter
public enum PaymentStatus {
  PAID, CANCELED, REFUNDED
}
