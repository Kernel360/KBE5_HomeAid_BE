package com.homeaid.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefundAdminDecisionRequestDto {

  @NotBlank(message = "관리자 코멘트는 필수입니다.")
  private String adminComment;
}