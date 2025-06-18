package com.homeaid.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCustomerSearchRequestDto {

  @Schema(description = "고객 ID")
  private Long userId;

  @Schema(description = "고객 이름")
  private String name;

  @Schema(description = "전화번호")
  private String phone;

}
