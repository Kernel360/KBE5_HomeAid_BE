package com.homeaid.payment.domain.factory;

import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.Refund;
import com.homeaid.payment.domain.enumerate.RefundReason;

public class RefundFactory {

  // 관리자 수동 환불 생성
  public static Refund createManualRefund(Payment payment, int amount, RefundReason reason, String adminComment) {
    return Refund.manualRefund(payment, amount, reason, adminComment);
  }

  // 고객 환불 요청 생성
  public static Refund createCustomerRequestedRefund(Payment payment, int amount, RefundReason reason, String customerComment) {
    return Refund.customerRequest(payment, amount, reason, customerComment);
  }

}
