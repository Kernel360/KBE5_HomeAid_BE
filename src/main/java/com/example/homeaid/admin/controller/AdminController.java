package com.example.homeaid.admin.controller;

import com.example.homeaid.admin.dto.response.CustomerListResponseDto;
import com.example.homeaid.admin.dto.response.ManagerListResponseDto;
import com.example.homeaid.admin.service.AdminService;
import com.example.homeaid.customer.entity.Customer;
import com.example.homeaid.global.common.response.CommonApiResponse;
import com.example.homeaid.global.util.PageUtil;
import com.example.homeaid.global.util.ResponsePagingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "고객 정보 조회", responses = {
        @ApiResponse(responseCode = "200", description = "유저 조회 성공",
            content = @Content(schema = @Schema(implementation = CustomerListResponseDto.class)))
    })
    @GetMapping("/customers")
    public ResponseEntity<CommonApiResponse<ResponsePagingDto<CustomerListResponseDto>>> getUserInfo(
        @ParameterObject
        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Customer> customerEntity = adminService.getCustomersBy(pageable);
        Page<CustomerListResponseDto> customerListResponseDto = customerEntity.map(
            CustomerListResponseDto::fromEntity);
        ResponsePagingDto<CustomerListResponseDto> customerPagingDto = PageUtil.from(
            customerListResponseDto);

        return ResponseEntity.ok(CommonApiResponse.success(customerPagingDto));
    }

    @GetMapping("/managers")
    public ResponseEntity<CommonApiResponse<ResponsePagingDto<ManagerListResponseDto>>> getManagerList(
        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ManagerListResponseDto> managerListDto = adminService.getManagersBy(pageable)
            .map(ManagerListResponseDto::fromEntity);
        ResponsePagingDto<ManagerListResponseDto> managerListPagingDto = PageUtil.from(
            managerListDto);

        return ResponseEntity.ok(CommonApiResponse.success(managerListPagingDto));
    }

}
