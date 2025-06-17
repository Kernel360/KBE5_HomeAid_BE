package com.homeaid.dto.request;

import com.homeaid.domain.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {

  @NotNull(message = "예약ID는 null이면 안됩니다!")
  private Long reservationId;

  @NotNull(message = "금액은 null이면 안됩니다!")
  private Integer amount;

  @NotNull(message = "결제수단은 null이면 안됩니다!")
  private PaymentMethod paymentMethod;

}
