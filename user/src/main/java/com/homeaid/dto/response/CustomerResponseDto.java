package com.homeaid.dto.response;

import com.homeaid.domain.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "고객 응답 DTO")
public class CustomerResponseDto {

  @Schema(description = "고객 ID", example = "1")
  private Long id;

  @Schema(description = "고객 이름", example = "Jane Doe")
  private String name;

  @Schema(description = "전화번호", example = "+66123456789")
  private String phone;

  @Schema(description = "이메일", example = "janedoe@example.com")
  private String email;

  @Schema(description = "가입일", example = "2025-06-06")
  private String signupDate;

  public static CustomerResponseDto toDto(Customer customer) {
    return CustomerResponseDto.builder()
        .id(customer.getId())
        .name(customer.getName())
        .phone(customer.getPhone())
        .email(customer.getEmail())
        .signupDate(customer.getCreatedAt() != null
            ? customer.getCreatedAt().toLocalDate().toString()
            : null)
        .build();
  }
}