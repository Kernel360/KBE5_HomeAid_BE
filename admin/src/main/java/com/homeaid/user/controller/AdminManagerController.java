package com.homeaid.user.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.common.response.PagedResponseDto;
import com.homeaid.dto.request.StatusChangeRequest;
import com.homeaid.dto.response.ManagerResponseDto;
import com.homeaid.user.service.AdminManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AdminManagerController", description = "관리자 매니저 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/managers")
public class AdminManagerController {

  private final AdminManagerService adminManagerService;

  @Operation(
      summary = "매니저 전체 목록 조회",
      description = "관리자가 모든 매니저를 페이징하여 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패")
  })
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CommonApiResponse<PagedResponseDto<ManagerResponseDto>>> getManagers(
      @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
      @RequestParam(value = "page", defaultValue = "0") int page,
      @Parameter(description = "페이지 크기", example = "10")
      @RequestParam(value = "size", defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    return ResponseEntity.ok(
        CommonApiResponse.success(
            PagedResponseDto.fromPage(adminManagerService.getAllManagers(pageable), ManagerResponseDto::toDto)
        )
    );
  }

  @Operation(
      summary = "매니저 상태 변환",
      description = "매니저의 상태를 변경합니다. (PENDING, REVIEW, ACTIVE, REJECTED)"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
      @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
      @ApiResponse(responseCode = "404", description = "대상 매니저를 찾을 수 없음")
  })
  @PatchMapping("/{id}/status")
  public ResponseEntity<CommonApiResponse<Void>> updateStatus(
      @Parameter(description = "상태를 변경할 매니저 ID", example = "1")
      @PathVariable Long id,
      @RequestBody @Valid StatusChangeRequest request
  ) {
    adminManagerService.updateStatus(id, request.getStatus());
    return ResponseEntity.ok(CommonApiResponse.success());
  }

//  @PatchMapping("/{id}/document-review")
//  @Operation(summary = "매니저 경력 파일 승인/반려", description = "관리자가 매니저가 업로드한 파일을 보고 승인하거나 반려합니다.")
//  @ApiResponses({
//      @ApiResponse(responseCode = "200", description = "파일 검토 완료"),
//      @ApiResponse(responseCode = "400", description = "요청 오류"),
//      @ApiResponse(responseCode = "404", description = "매니저 없음")
//  })
//  @PreAuthorize("hasRole('ADMIN')")
//  public ResponseEntity<CommonApiResponse<Void>> reviewDocument(
//      @PathVariable Long id,
//      @RequestBody @Valid DocumentReviewRequest request
//  ) {
//    adminManagerService.reviewDocument(id, request);
//    return ResponseEntity.ok(CommonApiResponse.success());
//  }
}
