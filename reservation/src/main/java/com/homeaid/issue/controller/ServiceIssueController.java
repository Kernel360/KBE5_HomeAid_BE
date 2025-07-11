package com.homeaid.issue.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.auth.user.CustomUserDetails;
import com.homeaid.issue.domain.ServiceIssue;
import com.homeaid.issue.dto.request.ServiceIssueRequestDto;
import com.homeaid.issue.dto.response.ServiceIssueResponseDto;
import com.homeaid.issue.service.ServiceIssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.FileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "ServiceIssue", description = "서비스 이슈 관리 API")
public class ServiceIssueController {

  private final ServiceIssueService serviceIssueService;

  @PostMapping(value = "/manager/reservations/{reservationId}/issues", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "서비스 이슈 생성", description = "매니저가 예약에 대한 서비스 이슈를 생성합니다.")
  public ResponseEntity<CommonApiResponse<Void>> createIssue(
      @PathVariable(name = "reservationId") Long reservationId,
      @ModelAttribute ServiceIssueRequestDto requestDto,
      @AuthenticationPrincipal CustomUserDetails user) {

    serviceIssueService.createIssue(
        reservationId,
        user.getUserId(),
        requestDto.getContent(),
        requestDto.getFiles()
    );

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(null));
  }

  @GetMapping("/reservations/{reservationId}/issues")
  @Operation(summary = "서비스 이슈 단건 조회", description = "이슈 ID로 서비스 이슈를 조회합니다.")
  public ResponseEntity<CommonApiResponse<ServiceIssueResponseDto>> getIssue(
      @PathVariable("reservationId") Long reservationId,
      @AuthenticationPrincipal CustomUserDetails user) {

    ServiceIssue serviceIssue = serviceIssueService.getIssueByReservation(reservationId,
        user.getUserId());

    return ResponseEntity.status(HttpStatus.OK)
        .body(CommonApiResponse.success(ServiceIssueResponseDto.toDto(serviceIssue)));
  }

  @PutMapping(value = "/manager/issues/{issueId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "서비스 이슈 수정", description = "매니저가 서비스 이슈를 수정합니다.")
  public ResponseEntity<CommonApiResponse<ServiceIssueResponseDto>> updateIssue(
      @PathVariable("issueId") Long issueId,
      @ModelAttribute ServiceIssueRequestDto requestDto,
      @AuthenticationPrincipal CustomUserDetails user) {

    ServiceIssue serviceIssue = serviceIssueService.updateIssue(
        issueId, user.getUserId(), requestDto.getContent(), requestDto.getFiles(),
        requestDto.getDeleteImageIds());

    return ResponseEntity.status(HttpStatus.OK)
        .body(CommonApiResponse.success(ServiceIssueResponseDto.toDto(serviceIssue)));
  }

  @DeleteMapping("/manager/issues/{issueId}")
  @Operation(summary = "서비스 이슈 삭제", description = "매니저가 서비스 이슈를 삭제합니다.")
  public ResponseEntity<CommonApiResponse<Void>> deleteIssue(
      @PathVariable("issueId") Long issueId,
      @AuthenticationPrincipal CustomUserDetails user) throws FileNotFoundException {

    serviceIssueService.deleteIssue(issueId, user.getUserId());

    return ResponseEntity.ok(CommonApiResponse.success(null));
  }

}
