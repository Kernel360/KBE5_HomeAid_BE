package com.homeaid.user.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.common.response.PagedResponseDto;
import com.homeaid.domain.Customer;
import com.homeaid.dto.request.CustomerResponseDto;
import com.homeaid.service.CustomerService;
import com.homeaid.user.dto.request.AdminCustomerSearchRequestDto;
import com.homeaid.user.service.AdminCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AdminCustomerController", description = "관리자 수요자 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/customers")
public class AdminCustomerController {

  private final AdminCustomerService adminCustomerService;

  @Operation(
      summary = "고객 전체 목록 조회",
      description = "관리자가 모든 고객을 페이징하여 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = CustomerResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CommonApiResponse<PagedResponseDto<CustomerResponseDto>>> getCustomers(
      @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
      @RequestParam(value = "page", defaultValue = "0") int page,
      @Parameter(description = "페이지 크기", example = "10")
      @RequestParam(value = "size", defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    AdminCustomerSearchRequestDto emptyDto = new AdminCustomerSearchRequestDto(); // 조건 없는 빈 dto

    return ResponseEntity.ok(
        CommonApiResponse.success(
            PagedResponseDto.fromPage(
                adminCustomerService.searchCustomers(emptyDto, pageable),
                CustomerResponseDto::toDto
            )
        )
    );
  }

  @GetMapping("/search")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CommonApiResponse<PagedResponseDto<CustomerResponseDto>>> getCustomers(
      @ModelAttribute AdminCustomerSearchRequestDto dto, // 👈 dto 자동 주입
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    Page<Customer> result = adminCustomerService.searchCustomers(dto, pageable);

    return ResponseEntity.ok(
        CommonApiResponse.success(
            PagedResponseDto.fromPage(result, CustomerResponseDto::toDto)
        )
    );
  }
}
