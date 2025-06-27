package com.homeaid.payment.service;

import com.homeaid.domain.User;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.PaymentStatus;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.payment.exception.PaymentErrorCode;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminPaymentServiceImpl implements AdminPaymentService {

  private final PaymentRepository paymentRepository;
  private final UserRepository userRepository;

  private PaymentResponseDto toDtoWithUserNames(Payment payment) {
    String customerName = userRepository.findById(payment.getReservation().getCustomerId())
        .map(User::getName)
        .orElse("알 수 없음");

    return PaymentResponseDto.toDto(payment, customerName);
  }

  @Override
  @Transactional(readOnly = true)
  public PaymentResponseDto getPayment(Long paymentId) {
    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));
    return toDtoWithUserNames(payment);
  }

  @Override
  @Transactional
  public PaymentResponseDto refundPayment(Long paymentId) { // 전체 환불
    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

    payment.refund(payment.getReservation().getStatus());

    return toDtoWithUserNames(payment); // 응답 DTO 변환
  }

  // TODO : 결제 도메인에 refundedAmount 추가했지만 아직 DB에 반영안됨. 추후 수정예정
  @Override
  @Transactional
  public PaymentResponseDto partialRefund(Long paymentId, int refundAmount) {
    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

    payment.partialRefund(payment.getReservation().getStatus(), refundAmount);

    return toDtoWithUserNames(payment);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PaymentResponseDto> getAllPayments() {
    return paymentRepository.findAll().stream()
        .map(this::toDtoWithUserNames)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<PaymentResponseDto> getAllPaymentsByCustomer(Long customerId) {
    return paymentRepository.findByCustomerId(customerId).stream()
        .map(this::toDtoWithUserNames)
        .collect(Collectors.toList());
  }

}