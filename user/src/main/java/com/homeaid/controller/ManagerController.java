package com.homeaid.controller;


import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.dto.request.ManagerDetailInfoRequestDto;
import com.homeaid.security.CustomUserDetails;
import com.homeaid.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Manager Controller", description = "매니저 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/managers/profile")
public class ManagerController {

  private final ManagerService managerService;

  @Operation(summary = "매니저 상세정보 등록", description = "매니저 선호 기능 및 가능한 조건을 등록합니다.")
  @PostMapping
  public ResponseEntity<CommonApiResponse<Void>> saveManagerDetailInfo(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestBody @Valid ManagerDetailInfoRequestDto requestDto) {

    Long managerId = user.getUserId();
    managerService.saveManagerDetailInfo(managerId, requestDto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success());
  }

}
