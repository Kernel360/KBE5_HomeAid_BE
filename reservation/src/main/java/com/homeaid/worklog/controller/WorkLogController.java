package com.homeaid.worklog.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.common.response.PagedResponseDto;
import com.homeaid.worklog.domain.WorkLog;
import com.homeaid.worklog.dto.request.WorkLogRequestDto;
import com.homeaid.worklog.dto.response.WorkLogResponseDto;
import com.homeaid.auth.user.CustomUserDetails;
import com.homeaid.worklog.service.WorkLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/managers/work-logs")
@RestController
public class WorkLogController {

  private final WorkLogService workLogService;

  @PatchMapping("/matchings/{matchingId}/check-in")
  @Operation(summary = "체크인 요청", description = "매니저의 매칭건에 대한 체크인 요청")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "체크인 성공"),
      @ApiResponse(responseCode = "400", description = "이미 체크인 한 매칭건"),
      @ApiResponse(responseCode = "403", description = "예약 위치 범위 밖의 잘못된 요청")
  })
  public ResponseEntity<CommonApiResponse<Void>> updateWorkLogForCheckIn(
      @AuthenticationPrincipal CustomUserDetails user,
      @Parameter(description = "매칭 ID", required = true)
      @PathVariable(name = "matchingId") Long matchingId,
      @RequestBody @Valid WorkLogRequestDto workLogRequestDto) {

    workLogService.updateWorkLogForCheckIn(user.getUserId(), matchingId, workLogRequestDto.getLat(),
        workLogRequestDto.getLng());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success());
  }

  @PatchMapping("/matchings/{matchingId}/check-out")
  @Operation(summary = "체크아웃 요청", description = "매니저의 매칭건에 대한 체크아웃 요청")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "체크아웃 성공"),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 매칭 건"),
      @ApiResponse(responseCode = "403", description = "체크인의 매니저와 체크아웃 매니저가 다를 때"),
      @ApiResponse(responseCode = "403", description = "예약 위치 범위 밖의 잘못된 요청")
  })
  public ResponseEntity<CommonApiResponse<Void>> updateWorkLogForCheckOut(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable(name = "matchingId") Long matchingId,
      @RequestBody @Valid WorkLogRequestDto workLogRequestDto) {

    workLogService.updateWorkLogForCheckOut(user.getUserId(), matchingId,
        workLogRequestDto.getLat(),
        workLogRequestDto.getLng());

    return ResponseEntity.ok(CommonApiResponse.success());
  }

  @GetMapping("/matchings/{matchingId}")
  @Operation(summary = "작업기록 조회", description = "매니저의 매칭건에 대한 작업기록 조회")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 매칭 건"),
      @ApiResponse(responseCode = "403", description = "체크인의 매니저와 체크아웃 매니저가 다를 때")
  })
  public ResponseEntity<CommonApiResponse<WorkLogResponseDto>> getWorkLog(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable(name = "matchingId") Long matchingId
  ) {

    return ResponseEntity.ok(CommonApiResponse.success(
        WorkLogResponseDto.toDto(workLogService.getWorkLog(user.getUserId(), matchingId))));
  }


  @GetMapping
  @Operation(summary = "매니저 근무 기록 전체 조회", description = "매니저의 체크인/체크아웃 기록을 페이지네이션으로 조회합니다.")
  public ResponseEntity<CommonApiResponse<PagedResponseDto<WorkLogResponseDto>>> getAllWorkLogsByManager(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);

    Page<WorkLog> workLogs = workLogService.getAllWorkLogsByManager(user.getUserId(), pageable);

    PagedResponseDto<WorkLogResponseDto> response = PagedResponseDto.fromPage(workLogs,
        WorkLogResponseDto::toDto);

    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

}
