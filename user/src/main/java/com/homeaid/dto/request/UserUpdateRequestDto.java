package com.homeaid.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 공통 정보 수정 요청 DTO")
public class UserUpdateRequestDto {

  @NotBlank(message = "이름은 필수입니다.")
  @Schema(description = "이름", example = "홍길동")
  private String name;

  @NotBlank(message = "이메일은 필수입니다.")
  @Email(message = "유효한 이메일 형식이어야 합니다.")
  @Schema(description = "이메일", example = "honggildong@example.com")
  private String email;

  @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)")
  @Schema(description = "전화번호", example = "010-1111-1111")
  private String phone;
}