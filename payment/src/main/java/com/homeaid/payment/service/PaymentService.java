package com.homeaid.payment.service;

import com.homeaid.payment.dto.request.PaymentRequestDto;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.payment.dto.response.ReservationPaymentDetailResponseDto;
import java.util.List;

public interface PaymentService {

  PaymentResponseDto pay(PaymentRequestDto dto);
  PaymentResponseDto customerCancelPayment(Long customerId, Long paymentId);
  List<PaymentResponseDto> getAllPayments(Long customerId);

  ReservationPaymentDetailResponseDto getReservationPaymentDetail(Long customerId, Long paymentId);

}
