package com.homeaid.user.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.UserWithdrawalRequest;
import com.homeaid.dto.response.UserWithdrawalResponseDto;
import com.homeaid.user.service.AdminWithdrawalRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/withdrawals")
@RequiredArgsConstructor
@Tag(name = "AdminWithdrawal", description = "관리자 탈퇴 요청 관리 API")
public class AdminWithdrawalRequestController {

  private final AdminWithdrawalRequestService adminWithdrawalRequestService;

  @GetMapping
  @Operation(summary = "탈퇴 요청 목록 조회", description = "보류 상태(PENDING)의 탈퇴 요청들을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  public ResponseEntity<CommonApiResponse<List<UserWithdrawalResponseDto>>> getPendingRequests() {
    List<UserWithdrawalRequest> requests = adminWithdrawalRequestService.getPendingRequests();
    List<UserWithdrawalResponseDto> response = requests.stream()
        .map(UserWithdrawalResponseDto::from)
        .toList();

    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  @DeleteMapping("/approve/{requestId}")
  @Operation(summary = "탈퇴 요청 승인", description = "해당 탈퇴 요청을 승인하고 회원을 탈퇴 처리합니다.")
  @ApiResponse(responseCode = "200", description = "탈퇴 승인 완료")
  public ResponseEntity<CommonApiResponse<Void>> approveRequest(
      @Parameter(description = "탈퇴 요청 ID") @PathVariable Long requestId
  ) {
    adminWithdrawalRequestService.processWithdrawal(requestId, true);
    return ResponseEntity.ok(CommonApiResponse.success());
  }

  @PatchMapping("/reject/{requestId}")
  @Operation(summary = "탈퇴 요청 반려", description = "해당 탈퇴 요청을 반려(거절) 처리합니다.")
  @ApiResponse(responseCode = "200", description = "탈퇴 반려 완료")
  public ResponseEntity<CommonApiResponse<Void>> rejectRequest(
      @Parameter(description = "탈퇴 요청 ID") @PathVariable Long requestId
  ) {
    adminWithdrawalRequestService.processWithdrawal(requestId, false);
    return ResponseEntity.ok(CommonApiResponse.success());
  }
}