package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.common.response.PagedResponseDto;
import com.homeaid.domain.CustomerAddress;
import com.homeaid.dto.request.CustomerAddressUpdateRequestDto;
import com.homeaid.dto.request.CustomerAddressSaveRequestDto;
import com.homeaid.dto.request.CustomerResponseDto;
import com.homeaid.dto.response.CustomerAddressResponseDto;
import com.homeaid.security.user.CustomUserDetails;
import com.homeaid.service.CustomerAddressService;
import com.homeaid.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customer Controller", description = "고객 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {

  private final CustomerAddressService customerAddressService;

  @Operation(
      summary = "저장된 주소 목록 조회",
      description = "로그인한 고객의 저장된 주소 목록을 조회합니다."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = CustomerAddressResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "고객을 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  @GetMapping("/addresses")
  public ResponseEntity<CommonApiResponse<List<CustomerAddressResponseDto>>> getSavedAddresses(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user) {

    Long customerId = user.getUserId();

    List<CustomerAddressResponseDto> addresses = customerAddressService.getSavedAddresses(customerId);

    return ResponseEntity.ok(CommonApiResponse.success(addresses));
  }

  @Operation(
      summary = "새 주소 저장",
      description = "새로운 주소를 저장합니다."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "저장 성공",
          content = @Content(schema = @Schema(implementation = CustomerAddressResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청",
      content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "고객을 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "409", description = "중복된 주소",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  @PostMapping("/addresses")
  public ResponseEntity<CommonApiResponse<CustomerAddressResponseDto>> saveAddress(
      @Valid @RequestBody CustomerAddressSaveRequestDto requestDto,
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user) {

    Long customerId = user.getUserId();

    CustomerAddress savedAddress = customerAddressService.saveAddress(customerId, CustomerAddressSaveRequestDto.toEntity(requestDto));

    return ResponseEntity.ok(CommonApiResponse.success(CustomerAddressResponseDto.toDto(savedAddress)));
  }


  @Operation(
      summary = "특정 주소 수정",
      description = "특정 주소를 수정합니다."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = CustomerAddressResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  @PutMapping("/addresses/{addressId}")
  public ResponseEntity<CommonApiResponse<CustomerAddressResponseDto>> updateAddress(
      @Parameter(description = "주소 ID", example = "1")
      @PathVariable Long addressId,
      @Valid @RequestBody CustomerAddressUpdateRequestDto requestDto,
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user) {

    Long customerId = user.getUserId();

    CustomerAddress updatedAddress = customerAddressService.updateAddress(customerId, addressId, CustomerAddressUpdateRequestDto.toEntity(requestDto));

    return ResponseEntity.ok(CommonApiResponse.success(CustomerAddressResponseDto.toDto(updatedAddress)));
  }

  @Operation(
      summary = "주소 삭제",
      description = "저장된 주소를 삭제합니다."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "삭제 성공",
          content = @Content(schema = @Schema(implementation = CustomerAddressResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  @DeleteMapping("/addresses/{addressId}")
  public ResponseEntity<CommonApiResponse<Void>> deleteAddress(
      @Parameter(description = "주소 ID", example = "1")
      @PathVariable Long addressId,
      @AuthenticationPrincipal CustomUserDetails user) {

    Long customerId = user.getUserId();

    customerAddressService.deleteAddress(customerId, addressId);

    return ResponseEntity.ok(CommonApiResponse.success(null));
  }

  // TODO admin 옮겨야 하는지?

}