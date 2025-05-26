package com.example.homeaid.admin.dto.response;

import com.example.homeaid.customer.customer.entity.Customer;
import com.example.homeaid.global.util.DateTimeUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerViewResponseDto {

    private Long id;
    private String email;
    private String name;
    private String phone;
    private String createdAt;

    public static CustomerViewResponseDto fromEntity(Customer customer) {
        return CustomerViewResponseDto.builder()
            .id(customer.getId())
            .email(customer.getEmail())
            .name(customer.getName())
            .phone(customer.getPhone())
            .createdAt(DateTimeUtil.formatToDate(customer.getCreatedAt()))
            .build();
    }
}



