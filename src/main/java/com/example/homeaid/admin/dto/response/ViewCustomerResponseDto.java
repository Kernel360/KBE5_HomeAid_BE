package com.example.homeaid.admin.dto.response;

import com.example.homeaid.customer.entity.Customer;
import com.example.homeaid.global.util.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewCustomerResponseDto {

    private Long id;
    private String email;
    private String name;
    private String phone;
    private String createdAt;

    public static ViewCustomerResponseDto fromEntity(Customer customer) {
        return ViewCustomerResponseDto.builder()
            .id(customer.getId())
            .email(customer.getUser().getEmail())
            .name(customer.getUser().getName())
            .phone(customer.getUser().getPhone())
            .createdAt(DateTimeUtil.localDateTimeUtil(customer.getUser().getCreatedAt()))
            .build();
    }

}
