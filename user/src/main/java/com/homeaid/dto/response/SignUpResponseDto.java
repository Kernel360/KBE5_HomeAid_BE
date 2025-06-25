package com.homeaid.dto.response;

import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignUpResponseDto {

  @Schema(description = "가입된 이름", example = "김고객")
  private String name;

  @Schema(description = "가입된 전화번호", example = "010-1111-1111")
  private String phone;

  @Schema(description = "가입 결과 메시지", example = "회원가입이 완료되었습니다.")
  private String message;


  public static SignUpResponseDto toManagerDto(Manager manager) {
    return SignUpResponseDto.builder()
        .name(manager.getName())
        .phone(manager.getPhone())
        .message("회원가입이 완료되었습니다.")
        .build();
  }

  public static SignUpResponseDto toCustomerDto(Customer customer) {
    return SignUpResponseDto.builder()
        .name(customer.getName())
        .phone(customer.getPhone())
        .message("회원가입이 완료되었습니다.")
        .build();
  }

}
