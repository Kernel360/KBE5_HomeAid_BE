package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.common.response.PagedResponseDto;
import com.homeaid.dto.request.CustomerResponseDto;
import com.homeaid.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customer Controller", description = "고객 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {

  private final CustomerService customerService;

  @Operation(
      summary = "고객 전체 목록 조회",
      description = "관리자가 모든 고객을 페이징하여 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패")
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

    return ResponseEntity.ok(
        CommonApiResponse.success(
            PagedResponseDto.fromPage(
                customerService.getAllCustomers(pageable),
                CustomerResponseDto::toDto
            )
        )
    );
  }
}