package com.homeaid.service;

import com.homeaid.dto.request.PaymentRequestDto;
import com.homeaid.dto.response.PaymentResponseDto;
import java.util.List;

public interface PaymentService {

  PaymentResponseDto pay(PaymentRequestDto dto);
  PaymentResponseDto customerCancelPayment(Long customerId, Long paymentId);
  PaymentResponseDto getPayment(Long customerId, Long paymentId);
  List<PaymentResponseDto> getAllPayments(Long customerId);

}
