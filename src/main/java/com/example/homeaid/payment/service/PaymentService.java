package com.example.homeaid.payment.service;

import com.example.homeaid.payment.dto.PaymentRequestDto;
import com.example.homeaid.payment.dto.PaymentResponseDto;

public interface PaymentService {

  PaymentResponseDto pay(PaymentRequestDto dto);

}
