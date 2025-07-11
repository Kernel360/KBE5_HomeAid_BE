package com.homeaid.payment.policy;

import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.dto.request.RefundRequestDto;
import com.homeaid.reservation.domain.Reservation;

public interface RefundAmountPolicy {

  // 이 정책이 해당 환불 요청에 적용 가능한지 판단
  boolean supports(RefundRequestDto dto, Reservation reservation);

  // 정책에 따라 환불금액 계산
  int calculateRefundAmount(RefundRequestDto dto, Reservation reservation, Payment payment);

}
// 환불 금액 계산 정책 인터페이스 - 각 정책별로 지원 여부와 환불금액 계산 로직을 정의