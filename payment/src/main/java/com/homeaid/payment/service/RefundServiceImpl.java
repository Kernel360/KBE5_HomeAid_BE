package com.homeaid.payment.service;

import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.Refund;
import com.homeaid.payment.domain.enumerate.RefundStatus;
import com.homeaid.payment.domain.factory.RefundFactory;
import com.homeaid.payment.dto.request.RefundRequestDto;
import com.homeaid.payment.dto.response.RefundResponseDto;
import com.homeaid.payment.exception.RefundErrorCode;
import com.homeaid.payment.policy.RefundPolicyCalculator;
import com.homeaid.payment.validator.RefundValidator;
import com.homeaid.payment.repository.RefundRepository;
import com.homeaid.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

  private final RefundRepository refundRepository;
  private final RefundValidator refundValidator;
  private final RefundPolicyCalculator refundPolicyCalculator;

  // 로그인한 회원이 자신의 환불 내역 전체를 조회
  @Override
  @Transactional(readOnly = true)
  public List<RefundResponseDto> getMyRefunds(Long userId) {
    List<Refund> refunds = refundRepository.findAllByPayment_Reservation_CustomerId(userId);
    return refunds.stream().map(RefundResponseDto::from).collect(Collectors.toList());
  }

  // 로그인한 회원이 자신의 특정 환불 내역 상세 조회
  @Override
  @Transactional(readOnly = true)
  public RefundResponseDto getMyRefundDetail(Long userId, Long refundId) {
    Refund refund = refundValidator.getRefundOrThrow(refundId, userId);
    return RefundResponseDto.from(refund);
  }

  // 로그인한 회원이 결제에 대한 환불 요청 생성
  @Override
  @Transactional
  public RefundResponseDto requestRefund(Long userId, RefundRequestDto requestDto) {
    Payment payment = refundValidator.getPaymentOrThrow(requestDto.getPaymentId());
    refundValidator.validateOwnership(payment, userId);
    refundValidator.validateDuplicateRefund(payment);

    int refundAmount = refundPolicyCalculator.calculate(requestDto, payment.getReservation(), payment);

    Refund refund = RefundFactory.createCustomerRequestedRefund(
        payment,
        refundAmount,
        requestDto.getReason(),
        requestDto.getCustomerComment()
    );

    Refund saved = refundRepository.save(refund);

    log.info("[환불요청] userId={} paymentId={} refundId={} amount={}", userId, payment.getId(), saved.getId(), refundAmount);

    return RefundResponseDto.from(saved);
  }

  @Override
  @Transactional
  public RefundResponseDto cancelRefundRequest(Long userId, Long refundId) {
    // 고객 본인의 환불 내역인지 검증 + 존재 여부 확인
    Refund refund = refundValidator.getRefundOrThrow(refundId, userId);

    // 요청 상태인 환불만 철회 가능하도록 검증
    if (refund.getStatus() != RefundStatus.REQUESTED) {
      throw new CustomException(RefundErrorCode.CANNOT_CANCEL_REFUND);
    }

    refund.cancel();
    Refund updated = refundRepository.save(refund);

    log.info("[환불철회] userId={} refundId={} status={}", userId, refundId, RefundStatus.CANCELLED);

    return RefundResponseDto.from(updated);
  }

}