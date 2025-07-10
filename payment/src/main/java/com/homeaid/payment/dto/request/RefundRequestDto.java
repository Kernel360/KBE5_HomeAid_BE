package com.homeaid.payment.dto.request;

import com.homeaid.payment.domain.enumerate.RefundReason;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RefundRequestDto {

  @NotNull(message = "결제 ID는 필수입니다.")
  private Long paymentId;           // 환불 대상 결제 ID

  @NotNull(message = "환불 사유는 필수입니다.")
  private RefundReason reason;      // 환불 요청 사유

  private String customerComment;   // 고객 환불 요청 사유 설명

}
