package com.example.homeaid.admin.controller;

import com.example.homeaid.admin.dto.response.ViewUserResponseDto;
import com.example.homeaid.admin.service.AdminService;
import com.example.homeaid.global.common.response.CommonApiResponse;
import com.example.homeaid.global.domain.Entity.User;
import com.example.homeaid.global.domain.Entity.User.Role;
import com.example.homeaid.global.exception.CustomException;
import com.example.homeaid.global.exception.ErrorCode;
import com.example.homeaid.global.util.PageResponse;
import com.example.homeaid.global.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "[관리자] 고객이나 매니저 정보 조회", responses = {
        @ApiResponse(responseCode = "200", description = "유저 조회 성공",
            content = @Content(schema = @Schema(implementation = ViewUserResponseDto.class)))
    })
    @GetMapping("/{user}")
    public ResponseEntity<CommonApiResponse<PageResponse<ViewUserResponseDto>>> getUserInfo(
        @Parameter(
            name = "user",
            description = "사용자 타입",
            schema = @Schema(allowableValues = {"customers", "managers"})
        )
        @PathVariable(required = true, value = "user") String user,
        @ParameterObject
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Role role = switch (user) {
            case "customers" -> Role.CUSTOMER;
            case "managers" -> Role.MANAGER;
            default -> throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        };
        Page<User> userPageEntity = adminService.findAllUser(role, pageable);
        Page<ViewUserResponseDto> pageDto = userPageEntity.map(ViewUserResponseDto::fromEntity);
        PageResponse<ViewUserResponseDto> pageResponse = PageUtil.from(pageDto);

        return ResponseEntity.ok(CommonApiResponse.success(pageResponse));
    }

}
