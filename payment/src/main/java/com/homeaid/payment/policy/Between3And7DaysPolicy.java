package com.homeaid.payment.policy;

import com.homeaid.domain.Reservation;
import com.homeaid.exception.CustomException;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.enumerate.RefundReason;
import com.homeaid.payment.dto.request.RefundRequestDto;
import com.homeaid.payment.exception.PaymentErrorCode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class Between3And7DaysPolicy implements RefundAmountPolicy{

  @Override
  public boolean supports(RefundRequestDto dto, Reservation reservation) {
    return dto.getReason() == RefundReason.BETWEEN_3_AND_7_DAYS;
  }

  @Override
  public int calculateRefundAmount(RefundRequestDto dto, Reservation reservation, Payment payment) {
    long daysUntilReservation = ChronoUnit.DAYS.between(LocalDate.now(), reservation.getRequestedDate());
    if (daysUntilReservation >= 3 && daysUntilReservation < 7) {
      return payment.getAmount() / 2;
    }
    throw new CustomException(PaymentErrorCode.REFUND_POLICY_VIOLATION);
  }

}
// 예약 3~7일 사이 50% 환불 정책