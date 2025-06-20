package com.homeaid.dto.response;

import com.homeaid.domain.UserWithdrawalRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserWithdrawalResponseDto {
  private Long id;
  private String reason;
  private String status;
  private SimpleUserDto user;

  public static UserWithdrawalResponseDto from(UserWithdrawalRequest entity) {
    return UserWithdrawalResponseDto.builder()
        .id(entity.getId())
        .reason(entity.getReason())
        .status(entity.getStatus().name())
        .user(SimpleUserDto.from(entity.getUser()))  // 요약 사용자만 전달
        .build();
  }
}
