package com.homeaid.dto.request;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.GenderType;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class ManagerSignUpRequestDto {

    @Schema(description = "이메일", example = "manager@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Schema(description = "비밀번호 (8자 이상, 영문+숫자+특수문자 포함)", example = "Password1!")
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
        message = "비밀번호는 8자 이상, 영문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @Schema(description = "이름", example = "김매니저")
    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하여야 합니다.")
    private String name;

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

    @Schema(description = "경력 (예: 간병인 5년)", example = "간병인 5년")
    private String career;

    @Schema(description = "경험", example = "노인 케어와 위생 관리에 자신 있습니다.")
    private String experience;

    public static Manager toEntity(
        ManagerSignUpRequestDto managerSignUpRequestDto, String encodedPassword) {
        return Manager.builder()
            .email(managerSignUpRequestDto.getEmail())
            .password(encodedPassword)
            .name(managerSignUpRequestDto.getName())
            .phone(managerSignUpRequestDto.getPhone())
            .birth(managerSignUpRequestDto.getBirth())
            .gender(managerSignUpRequestDto.getGender())
            .career(managerSignUpRequestDto.getCareer())
            .experience(managerSignUpRequestDto.getExperience())
            .build();
    }
}
