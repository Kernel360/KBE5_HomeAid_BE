package com.example.homeaid.admin.controller;

import com.example.homeaid.admin.dto.response.CustomerViewResponseDto;
import com.example.homeaid.admin.dto.response.ManagerViewResponseDto;
import com.example.homeaid.admin.service.AdminService;
import com.example.homeaid.global.common.response.CommonApiResponse;
import com.example.homeaid.global.util.PageUtil;
import com.example.homeaid.global.util.ResponsePagingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "고객 정보 조회", responses = {
        @ApiResponse(responseCode = "200", description = "유저 조회 성공",
            content = @Content(schema = @Schema(implementation = CustomerViewResponseDto.class)))
    })
    @GetMapping("/customers")
    public ResponseEntity<CommonApiResponse<ResponsePagingDto<CustomerViewResponseDto>>> getUserInfo(
        @ParameterObject
        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CustomerViewResponseDto> customerViewResponseDto = adminService.getCustomersBy(pageable)
            .map(CustomerViewResponseDto::fromEntity);

        ResponsePagingDto<CustomerViewResponseDto> customerPagingDto = PageUtil.from(
            customerViewResponseDto);

        return ResponseEntity.ok(CommonApiResponse.success(customerPagingDto));
    }

    @GetMapping("/managers")
    public ResponseEntity<CommonApiResponse<ResponsePagingDto<ManagerViewResponseDto>>> getManagerList(
        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ManagerViewResponseDto> managerListDto = adminService.getManagersBy(pageable)
            .map(ManagerViewResponseDto::fromEntity);

        ResponsePagingDto<ManagerViewResponseDto> managerViewPagingDto = PageUtil.from(
            managerListDto);

        return ResponseEntity.ok(CommonApiResponse.success(managerViewPagingDto));
    }

}
