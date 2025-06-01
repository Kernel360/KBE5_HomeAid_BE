package com.homeaid.service;

import com.homeaid.dto.request.PaymentRequestDto;
import com.homeaid.dto.response.PaymentResponseDto;

public interface PaymentService {

  PaymentResponseDto pay(PaymentRequestDto dto);

}
