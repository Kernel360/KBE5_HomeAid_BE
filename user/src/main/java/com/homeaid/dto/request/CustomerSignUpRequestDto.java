package com.homeaid.dto.request;

import com.homeaid.domain.Customer;
import com.homeaid.domain.CustomerAddress;
import com.homeaid.domain.enumerate.GenderType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerSignUpRequestDto {

  @Email(message = "올바른 이메일 형식이 아닙니다.")
  @NotBlank(message = "이메일은 필수 입력값입니다.")
  private String email;

  @NotBlank(message = "비밀번호는 필수 입력값입니다.")
  @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
      message = "비밀번호는 8자 이상, 영문자, 숫자, 특수문자를 포함해야 합니다."
  )
  private String password;

  @NotBlank(message = "이름은 필수 입력값입니다.")
  @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하여야 합니다.")
  private String name;

  @NotBlank(message = "전화번호는 필수 입력값입니다.")
  @Pattern(
      regexp = "^01[016789]-\\d{3,4}-\\d{4}$",
      message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678"
  )
  private String phone;

  @NotNull(message = "생년월일은 필수 입력값입니다.")
  @Past(message = "생년월일은 과거 날짜여야 합니다.")
  private LocalDate birth;

  @NotNull(message = "성별은 필수 입력값입니다.")
  private GenderType gender;

  @NotBlank(message = "주소는 필수 입력값입니다.")
  private String address;

  @NotBlank(message = "상세 주소는 필수 입력값입니다.")
  private String addressDetail;


  // TODO Security 추가 시 파라미터 주석 해제
  public static Customer toEntity(
      CustomerSignUpRequestDto customerSignUpRequestDto /*, String encodedPassword*/) {
    return Customer.addSingleAddress()
        .email(customerSignUpRequestDto.getEmail())
        .password(customerSignUpRequestDto.getPassword()) // encodedPassword로 수정 필요
        .name(customerSignUpRequestDto.getName())
        .phone(customerSignUpRequestDto.getPhone())
        .birth(customerSignUpRequestDto.getBirth())
        .gender(customerSignUpRequestDto.getGender())
        .address(CustomerAddress.builder()
            .address(customerSignUpRequestDto.getAddress())
            .addressDetail(customerSignUpRequestDto.getAddressDetail())
            .build())
        .build();
  }

}
