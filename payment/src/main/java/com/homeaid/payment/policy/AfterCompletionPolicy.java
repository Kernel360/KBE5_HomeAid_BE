package com.homeaid.payment.policy;

import com.homeaid.domain.Reservation;
import com.homeaid.exception.CustomException;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.enumerate.RefundReason;
import com.homeaid.payment.dto.request.RefundRequestDto;
import com.homeaid.payment.exception.RefundErrorCode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class AfterCompletionPolicy implements RefundAmountPolicy{

  @Override
  public boolean supports(RefundRequestDto dto, Reservation reservation) {
    return dto.getReason() == RefundReason.AFTER_COMPLETION || dto.getReason() == RefundReason.CUSTOMER_DISSATISFACTION;
  }

  @Override
  public int calculateRefundAmount(RefundRequestDto dto, Reservation reservation, Payment payment) {
    LocalDate today = LocalDate.now();
    LocalDate completedDate = reservation.getRequestedDate(); // 필요 시 Reservation에 완료일 필드 추가 권장
    long daysSinceCompletion = ChronoUnit.DAYS.between(completedDate, today);

    if (daysSinceCompletion <= 3) {
      return payment.getAmount() / 2;
    }
    throw new CustomException(RefundErrorCode.REFUND_REQUEST_PERIOD_EXCEEDED);
  }

}
// 서비스 완료 후, 또는 고객 불만족에 의한 환불 정책