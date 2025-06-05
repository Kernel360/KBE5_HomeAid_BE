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

  @Schema(description = "가입된 이메일", example = "user@example.com")
  private String email;

  @Schema(description = "가입 결과 메시지", example = "회원가입이 완료되었습니다.")
  private String message;


  public static SignUpResponseDto toManagerDto(Manager manager) {
    return SignUpResponseDto.builder()
        .email(manager.getEmail())
        .message("회원가입이 완료되었습니다.")
        .build();
  }

  public static SignUpResponseDto toCustomerDto(Customer customer) {
    return SignUpResponseDto.builder()
        .email(customer.getEmail())
        .message("회원가입이 완료되었습니다.")
        .build();
  }

}
