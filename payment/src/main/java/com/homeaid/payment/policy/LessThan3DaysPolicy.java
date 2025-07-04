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
public class LessThan3DaysPolicy implements RefundAmountPolicy{

  @Override
  public boolean supports(RefundRequestDto dto, Reservation reservation) {
    return dto.getReason() == RefundReason.LESS_THAN_3_DAYS;
  }

  @Override
  public int calculateRefundAmount(RefundRequestDto dto, Reservation reservation, Payment payment) {
    long daysUntilReservation = ChronoUnit.DAYS.between(LocalDate.now(), reservation.getRequestedDate());
    if (daysUntilReservation < 3) {
      return (int) (payment.getAmount() * 0.3);
    }
    throw new CustomException(PaymentErrorCode.REFUND_POLICY_VIOLATION);
  }

}
// 예약 3일 이내 요청 시 30% 환불 정책