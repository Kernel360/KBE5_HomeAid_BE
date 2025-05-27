package com.homeaid.domain;

import lombok.Getter;

@Getter
public enum ReservationStatus {

  REQUESTED("예약 요청됨"),
  MATCHING("매칭 중"),
  MATCHED("매칭 완료"),
  COMPLETED("서비스 완료"),
  CANCELLED("예약 취소됨");

  private final String description;

  ReservationStatus(String description) {
    this.description = description;
  }

}
