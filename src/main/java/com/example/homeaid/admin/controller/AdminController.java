package com.example.homeaid.admin.controller;

import com.example.homeaid.admin.dto.response.ViewCustomerResponseDto;
import com.example.homeaid.admin.service.AdminService;
import com.example.homeaid.global.common.response.CommonApiResponse;
import com.example.homeaid.global.util.PageResponse;
import com.example.homeaid.global.util.PageUtil;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/customers")
    public ResponseEntity<CommonApiResponse<PageResponse<ViewCustomerResponseDto>>> customerManage(
        @PageableDefault(size = 10, sort = "user.createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ViewCustomerResponseDto> pageDto = adminService.findAllCustomer(pageable)
            .map(ViewCustomerResponseDto::fromEntity);

        PageResponse<ViewCustomerResponseDto> viewCustomerResponsePage = PageUtil.from(pageDto);


        return ResponseEntity.ok(CommonApiResponse.success(viewCustomerResponsePage));
    }

}
