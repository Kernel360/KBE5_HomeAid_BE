package com.homeaid.payment.dto.response;

import com.homeaid.payment.domain.Refund;
import com.homeaid.payment.domain.enumerate.RefundReason;
import com.homeaid.payment.domain.enumerate.RefundStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefundResponseDto {

  private Long refundId;
  private Long paymentId;
  private Integer refundAmount;
  private RefundReason reason;
  private RefundStatus status;
  private String customerComment;
  private String adminComment;
  private LocalDateTime requestedAt;
  private LocalDateTime processedAt;

  public static RefundResponseDto from(Refund refund) {
    return RefundResponseDto.builder()
        .refundId(refund.getId())
        .paymentId(refund.getPayment().getId())
        .refundAmount(refund.getRefundAmount())
        .reason(refund.getReason())
        .status(refund.getStatus())
        .customerComment(refund.getCustomerComment())
        .adminComment(refund.getAdminComment())
        .requestedAt(refund.getRequestedAt())
        .processedAt(refund.getProcessedAt())
        .build();
  }
}