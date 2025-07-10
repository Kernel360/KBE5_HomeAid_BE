package com.homeaid.payment.service;

import com.homeaid.payment.dto.request.RefundRequestDto;
import com.homeaid.payment.dto.response.RefundResponseDto;
import java.util.List;

public interface RefundService {

  // 고객이 환불요청
  RefundResponseDto requestRefund(Long userId, RefundRequestDto requestDto);

  // 환불내역 전체조회
  List<RefundResponseDto> getMyRefunds(Long userId);

  // 환불내역 상세보기
  RefundResponseDto getMyRefundDetail(Long userId, Long refundId);

  // 환불요청 철회
  RefundResponseDto cancelRefundRequest(Long userId, Long refundId);
}
