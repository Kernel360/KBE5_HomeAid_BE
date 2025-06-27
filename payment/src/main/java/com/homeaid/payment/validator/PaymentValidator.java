package com.homeaid.payment.validator;

import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.exception.CustomException;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.exception.PaymentErrorCode;
import com.homeaid.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentValidator {

  private final PaymentRepository paymentRepository;

  public void validatePaymentOwnership(Payment payment, Long customerId) {
    if (!payment.getReservation().getCustomerId().equals(customerId)) {
      throw new CustomException(PaymentErrorCode.PAYMENT_ACCESS_DENIED);
    }
  }

  public void validateNotAlreadyPaid(Long reservationId) {
    if (paymentRepository.existsByReservationId(reservationId)) {
      throw new CustomException(PaymentErrorCode.PAYMENT_ALREADY_PAID);
    }
  }

  public void validateReservableStatus(ReservationStatus status) {
    switch (status) {
      case MATCHED -> {}
      case COMPLETED -> throw new CustomException(PaymentErrorCode.PAYMENT_ALREADY_PAID);
      default -> throw new CustomException(PaymentErrorCode.PAYMENT_INVALID_STATUS_FOR_PAYMENT);
    }
  }
}