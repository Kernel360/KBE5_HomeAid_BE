package com.homeaid.dto.response;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "매니저 응답 DTO")
public class ManagerResponseDto {

  @Schema(description = "매니저 ID", example = "1")
  private Long id;

  @Schema(description = "매니저 이름", example = "홍길동")
  private String name;

  @Schema(description = "전화번호", example = "010-1111-1111")
  private String phone;

  @Schema(description = "이메일", example = "johndoe@example.com")
  private String email;

  @Schema(description = "경력", example = "5 years")
  private String career;

  @Schema(description = "경험", example = "청소, 가사도우미")
  private String experience;

  @Schema(description = "상태", example = "ACTIVE")
  private ManagerStatus status;

  @Schema(description = "가입일", example = "2025-06-06")
  private String signupDate;

  @Schema(description = "반려 사유", example = "파일이 명확하지 않습니다.")
  private String rejectionReason;

  @Schema(description = "프로필 사진", example = "이미지.jpg")
  private String profileImageUrl;

  public static ManagerResponseDto toDto(Manager manager) {
    return ManagerResponseDto.builder()
        .id(manager.getId())
        .name(manager.getName())
        .phone(manager.getPhone())
        .email(manager.getEmail())
        .career(manager.getCareer())
        .experience(manager.getExperience())
        .status(manager.getStatus())
        .signupDate(manager.getCreatedAt() != null
            ? manager.getCreatedAt().toLocalDate().toString()
            : null)
        .rejectionReason(manager.getRejectionReason())
        .profileImageUrl(manager.getProfileImageUrl())
        .build();
  }
}