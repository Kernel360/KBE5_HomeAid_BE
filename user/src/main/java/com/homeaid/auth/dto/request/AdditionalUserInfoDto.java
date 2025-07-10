package com.homeaid.auth.dto.request;

import com.homeaid.domain.enumerate.GenderType;
import com.homeaid.domain.enumerate.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalUserInfoDto {

  @Schema(description = "이메일", example = "example@gmail.com")
  @NotBlank(message = "이메일은 필수 입력값입니다.")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  private String email;

  @Schema(description = "권한", example = "CUSTOMER")
  @NotNull(message = "사용자 권한을 선택해야 합니다.")
  private UserRole role;

  @Schema(description = "전화번호", example = "010-4321-5678")
  @NotBlank(message = "전화번호는 필수 입력값입니다.")
  @Pattern(
      regexp = "^01[016789]-\\d{3,4}-\\d{4}$",
      message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678"
  )
  private String phone;

  @Schema(description = "생년월일", example = "1985-05-05", type = "string", format = "date")
  @NotNull(message = "생년월일은 필수 입력값입니다.")
  @Past(message = "생년월일은 과거 날짜여야 합니다.")
  private LocalDate birth;

  @Schema(description = "성별", example = "MALE")
  @NotNull(message = "성별은 필수 입력값입니다.")
  private GenderType gender;

}
