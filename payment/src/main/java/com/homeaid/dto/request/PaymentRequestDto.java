package com.homeaid.dto.request;

import com.homeaid.domain.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

  @NotNull
  @Schema(description = "예약 ID", example = "1001") // example 값을 작은 값으로 고정!
  private Long reservationId;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal amount;

  @NotNull
  private PaymentMethod paymentMethod;

}
