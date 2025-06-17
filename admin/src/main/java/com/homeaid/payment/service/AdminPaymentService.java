package com.homeaid.payment.service;

import com.homeaid.payment.dto.response.PaymentResponseDto;
import java.util.List;

public interface AdminPaymentService {
  PaymentResponseDto getPayment(Long paymentId);
  PaymentResponseDto refundPayment(Long paymentId); // 전체 환불
  PaymentResponseDto partialRefund(Long paymentId, int refundAmount); // 부분 환불
  List<PaymentResponseDto> getAllPayments();
  List<PaymentResponseDto> getAllPaymentsByCustomer(Long customerId);

}
