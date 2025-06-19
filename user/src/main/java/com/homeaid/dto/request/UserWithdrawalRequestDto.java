package com.homeaid.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 탈퇴 요청 DTO")
public class UserWithdrawalRequestDto {

  @Schema(description = "탈퇴 사유", example = "서비스가 마음에 들지 않아요.")
  @NotBlank
  private String reason;

}
