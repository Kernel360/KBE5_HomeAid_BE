package com.homeaid.dto.response;

import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSignUpResponseDto {

  private String email;
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
