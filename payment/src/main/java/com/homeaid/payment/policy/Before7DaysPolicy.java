package com.homeaid.payment.policy;

import com.homeaid.domain.Reservation;
import com.homeaid.exception.CustomException;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.enumerate.RefundReason;
import com.homeaid.payment.dto.request.RefundRequestDto;
import com.homeaid.payment.exception.PaymentErrorCode;
import com.homeaid.payment.exception.RefundErrorCode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class Before7DaysPolicy implements RefundAmountPolicy{

  @Override
  public boolean supports(RefundRequestDto dto, Reservation reservation) {
    return dto.getReason() == RefundReason.BEFORE_7_DAYS;
  }

  @Override
  public int calculateRefundAmount(RefundRequestDto dto, Reservation reservation, Payment payment) {
    long daysUntilReservation = ChronoUnit.DAYS.between(LocalDate.now(), reservation.getRequestedDate());
    if (daysUntilReservation >= 7) {
      return payment.getAmount();
    }
    throw new CustomException(RefundErrorCode.REFUND_POLICY_VIOLATION);
  }

}
// 예약 7일 이전 전액 환불 정책