package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.dto.request.UserWithdrawalRequestDto;
import com.homeaid.service.UserWithdrawalRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/my/withdrawal")
@RequiredArgsConstructor
@Tag(name = "Withdrawal", description = "수요자, 매니저 탈퇴 요청 API")
public class UserWithdrawalController {

  private final UserWithdrawalRequestService withdrawalRequestService;

  @PostMapping("/{userId}")
  @Operation(summary = "회원 탈퇴 요청", description = "회원이 탈퇴 사유와 함께 탈퇴 요청을 보냅니다.")
  @ApiResponse(responseCode = "200", description = "탈퇴 요청 완료")
  public ResponseEntity<CommonApiResponse<Void>> requestWithdrawal(
      @PathVariable Long userId,
      @Valid @RequestBody UserWithdrawalRequestDto dto
  ) {
    withdrawalRequestService.requestWithdrawal(userId, dto);
    return ResponseEntity.ok(CommonApiResponse.success());
  }
}
