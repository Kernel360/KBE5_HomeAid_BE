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
public class UserSignUpResponseDto {

  @Schema(description = "가입된 이메일", example = "user@example.com")
  private String email;

  @Schema(description = "가입 결과 메시지", example = "회원가입이 완료되었습니다.")
  private String message;


  public static UserSignUpResponseDto toManagerDto(Manager manager) {
    return UserSignUpResponseDto.builder()
        .email(manager.getEmail())
        .message("회원가입이 완료되었습니다.")
        .build();
  }

  public static UserSignUpResponseDto toCustomerDto(Customer customer) {
    return UserSignUpResponseDto.builder()
        .email(customer.getEmail())
        .message("회원가입이 완료되었습니다.")
        .build();
  }

}
