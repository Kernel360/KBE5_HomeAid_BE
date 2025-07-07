package com.homeaid.payment.service;

import com.homeaid.payment.dto.request.RefundPartialFlexibleRequestDto;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.payment.dto.response.RefundResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminRefundService {

  PaymentResponseDto refundFull(Long paymentId); // 전액 환불
  PaymentResponseDto refundPartial(Long paymentId, RefundPartialFlexibleRequestDto request); // 부분 환불
  RefundResponseDto approveRefund(Long refundId, String adminComment); // 환불 승인
  RefundResponseDto rejectRefund(Long refundId, String adminComment);  // 환불 거절

  Page<RefundResponseDto> getAllRefunds(Pageable pageable);
  Page<RefundResponseDto> getRefundsByUserId(Long userId, Pageable pageable);
  RefundResponseDto getRefundDetail(Long refundId);
}