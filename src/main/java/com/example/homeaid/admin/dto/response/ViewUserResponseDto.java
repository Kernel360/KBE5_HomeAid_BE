package com.example.homeaid.admin.dto.response;

import com.example.homeaid.global.domain.Entity.User;
import com.example.homeaid.global.util.DateTimeUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ViewUserResponseDto {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String role;
    private String createdAt;

    public static ViewUserResponseDto fromEntity(User user) {
        return ViewUserResponseDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .role(user.getRole().name())
            .createdAt(DateTimeUtil.formatToDate(user.getCreatedAt()))
            .build();
    }
}
