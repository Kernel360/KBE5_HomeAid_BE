package com.homeaid.dto.response;

import com.homeaid.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetUserProfileResponseDto {

  @Schema(description = "이름", example = "홍길동")
  private String name;

  @Schema(description = "이메일", example = "honggildong@example.com")
  private String email;

  @Schema(description = "전화번호", example = "010-1111-1111")
  private String phone;

  @Schema(description = "프로필 사진", example = "이미지.jpg")
  private String profileImageUrl;

  public static GetUserProfileResponseDto toUserProfileDto (User user) {
    return GetUserProfileResponseDto.builder()
        .name(user.getName())
        .email(user.getEmail())
        .phone(user.getPhone())
        .profileImageUrl(user.getProfileImageUrl())
        .build();
  }
}
