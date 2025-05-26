package com.example.homeaid.admin.dto.response;

import com.example.homeaid.global.util.DateTimeUtil;
import com.example.homeaid.manager.manager.entity.Manager;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ManagerViewResponseDto {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String createdAt;

    public static ManagerViewResponseDto fromEntity(Manager manager) {
        return ManagerViewResponseDto.builder()
            .id(manager.getId())
            .email(manager.getEmail())
            .name(manager.getName())
            .createdAt(DateTimeUtil.formatToDate(manager.getCreatedAt()))
            .build();
    }

}
