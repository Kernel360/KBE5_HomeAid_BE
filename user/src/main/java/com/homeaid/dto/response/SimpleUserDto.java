package com.homeaid.dto.response;

import com.homeaid.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SimpleUserDto {
  private Long id;
  private String email;
  private String name;
  private String phone;
  private String role;

  public static SimpleUserDto from(User user) {
    return SimpleUserDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .name(user.getName())
        .phone(user.getPhone())
        .role(user.getRole().name())
        .build();
  }

}
