package com.homeaid.payment.service;

import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.payment.dto.response.RefundResponseDto;

public interface AdminRefundService {

  PaymentResponseDto refundFull(Long paymentId);
  PaymentResponseDto refundPartial(Long paymentId, int refundAmount);

  RefundResponseDto approveRefund(Long refundId, String adminComment); // 환불 승인
  RefundResponseDto rejectRefund(Long refundId, String adminComment);  // 환불 거절
}
