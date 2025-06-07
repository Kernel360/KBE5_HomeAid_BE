package com.homeaid.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignInRequestDto {

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    @Schema(description = "전화번호", example = "010-1111-1111")
    private String phone;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Schema(description = "비밀번호 (8자 이상, 영문+숫자+특수문자 포함)", example = "Password1!")
    private String password;

}
