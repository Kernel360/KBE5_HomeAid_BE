package com.example.homeaid.admin.dto.response;

import com.example.homeaid.customer.entity.Customer;
import com.example.homeaid.global.util.DateTimeUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerListResponseDto {

    private Long id;
    private String email;
    private String name;
    private String phone;
    private String createdAt;

    public static CustomerListResponseDto fromEntity(Customer customer) {
        return CustomerListResponseDto.builder()
            .id(customer.getId())
            .email(customer.getEmail())
            .name(customer.getName())
            .phone(customer.getPhone())
            .createdAt(DateTimeUtil.formatToDate(customer.getCreatedAt()))
            .build();
    }
}



