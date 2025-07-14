package com.homeaid.auth.dto.request;

import com.homeaid.domain.enumerate.GenderType;
import com.homeaid.domain.enumerate.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthSignupRequestDto {

  @NotBlank(message = "OAuth 임시 코드는 필수입니다.")
  private String oauthCode;

  @NotNull(message = "사용자 역할은 필수입니다.")
  private UserRole role;

  @NotBlank(message = "전화번호는 필수입니다.")
  @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
  private String phone;

  @NotNull(message = "생년월일은 필수입니다.")
  @Past(message = "생년월일은 과거 날짜여야 합니다.")
  private LocalDate birth;

  @NotNull(message = "성별은 필수입니다.")
  private GenderType gender;

  // Manager 전용 필드
  private String career;
  private String experience;

}
