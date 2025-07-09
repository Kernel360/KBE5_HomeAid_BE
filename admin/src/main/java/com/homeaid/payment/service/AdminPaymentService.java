package com.homeaid.payment.service;

import com.homeaid.payment.dto.response.PaymentResponseDto;
import java.util.List;

public interface AdminPaymentService {
  PaymentResponseDto getPayment(Long paymentId);
  List<PaymentResponseDto> getAllPayments();
  List<PaymentResponseDto> getAllPaymentsByCustomer(Long customerId);

}
