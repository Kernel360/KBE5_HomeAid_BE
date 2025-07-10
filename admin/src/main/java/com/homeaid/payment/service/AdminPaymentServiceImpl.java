package com.homeaid.payment.service;

import com.homeaid.domain.User;
import com.homeaid.payment.domain.Payment;
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