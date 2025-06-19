package com.homeaid.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminCustomerSearchRequestDto {

  @Schema(description = "고객 ID")
  private Long userId;

  @Schema(description = "고객 이름")
  private String name;

  @Schema(description = "전화번호")
  private String phone;

}
