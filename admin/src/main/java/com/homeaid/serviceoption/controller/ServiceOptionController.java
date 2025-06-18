package com.homeaid.serviceoption.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.serviceoption.dto.request.ServiceOptionRequestDto;
import com.homeaid.serviceoption.dto.response.ServiceOptionDetailDto;
import com.homeaid.serviceoption.dto.response.ServiceOptionResponseDto;
import com.homeaid.serviceoption.service.ServiceOptionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/service-option")
@Tag(name = "Admin Service Option", description = "관리자 서비스 옵션 관리 API")
public class ServiceOptionController {

  private final ServiceOptionServiceImpl serviceOptionService;

  @PostMapping
  @Operation(summary = "서비스 옵션 등록", description = "새로운 서비스 옵션을 등록합니다.")
  @ApiResponse(responseCode = "201", description = "옵션 등록 성공",
      content = @Content(schema = @Schema(implementation = ServiceOptionResponseDto.class)))
  public ResponseEntity<CommonApiResponse<ServiceOptionResponseDto>> createOption(
      @RequestBody @Valid ServiceOptionRequestDto serviceOptionRequestDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(ServiceOptionResponseDto.toDto(
            serviceOptionService.createOption(
                ServiceOptionRequestDto.toEntity(serviceOptionRequestDto)))));
  }

  @GetMapping
  @Operation(summary = "서비스 옵션 목록 조회", description = "등록된 모든 서비스 옵션을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "옵션 목록 조회 성공",
      content = @Content(schema = @Schema(implementation = ServiceOptionResponseDto.class)))
  public ResponseEntity<CommonApiResponse<List<ServiceOptionResponseDto>>> getAllOptions() {
    return ResponseEntity.ok(CommonApiResponse.success(
        serviceOptionService.getAllOptions().stream().map(ServiceOptionResponseDto::toDto)
            .toList()));
  }

  @PatchMapping("/{optionId}")
  @Operation(summary = "서비스 옵션 수정", description = "서비스 옵션의 이름, 설명, 가격을 수정합니다.")
  @ApiResponse(responseCode = "200", description = "옵션 수정 성공",
      content = @Content(schema = @Schema(implementation = ServiceOptionResponseDto.class)))
  public ResponseEntity<CommonApiResponse<ServiceOptionResponseDto>> updateOption(
      @Parameter(description = "옵션 ID", example = "1")
      @PathVariable(name = "optionId") Long optionId,
      @RequestBody @Valid ServiceOptionRequestDto serviceOptionRequestDto) {
    return ResponseEntity.ok(CommonApiResponse.success(
        ServiceOptionResponseDto.toDto(serviceOptionService.updateOption(optionId,
            ServiceOptionRequestDto.toEntity(serviceOptionRequestDto)))));
  }

  @DeleteMapping("/{optionId}")
  @Operation(summary = "서비스 옵션 삭제", description = "해당 서비스 옵션을 삭제합니다.")
  @ApiResponse(responseCode = "200", description = "옵션 삭제 성공")
  public ResponseEntity<CommonApiResponse<Void>> deleteOption(
      @Parameter(description = "옵션 ID", example = "1")
      @PathVariable(name = "optionId") Long optionId) {
    serviceOptionService.deleteOption(optionId);
    return ResponseEntity.ok(CommonApiResponse.success(null));
  }
}