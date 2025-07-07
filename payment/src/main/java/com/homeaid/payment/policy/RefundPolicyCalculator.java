package com.homeaid.payment.policy;

import com.homeaid.domain.Reservation;
import com.homeaid.exception.CustomException;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.dto.request.RefundRequestDto;
import com.homeaid.payment.exception.RefundErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefundPolicyCalculator {

  private final List<RefundAmountPolicy> policies;

  public int calculate(RefundRequestDto dto, Reservation reservation, Payment payment) {
    return policies.stream()
        .filter(policy -> policy.supports(dto, reservation))
        .findFirst()
        .orElseThrow(() -> new CustomException(RefundErrorCode.INVALID_REFUND_REASON))
        .calculateRefundAmount(dto, reservation, payment);
  }

}
// 모든 환불 정책을 위임해 환불금액을 계산하는 메인 클래스