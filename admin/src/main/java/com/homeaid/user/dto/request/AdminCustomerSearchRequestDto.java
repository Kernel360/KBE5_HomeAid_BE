package com.homeaid.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCustomerSearchRequestDto {

  @Schema(description = "고객 이름 (부분 일치 검색)")
  private String name;

  @Schema(description = "고객 이메일 (부분 일치 검색)")
  private String email;

  @Schema(description = "전화번호 (부분 일치 검색)")
  private String phone;

}
