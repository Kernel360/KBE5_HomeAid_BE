package com.homeaid.user.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.common.response.PagedResponseDto;
import com.homeaid.domain.Manager;
import com.homeaid.domain.ManagerDocument;
import com.homeaid.dto.request.StatusChangeRequest;
import com.homeaid.dto.response.ManagerDocumentListResponseDto;
import com.homeaid.dto.response.ManagerResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ErrorCode;
import com.homeaid.repository.ManagerDocumentRepository;
import com.homeaid.user.dto.request.AdminDocumentReviewRequest;
import com.homeaid.user.dto.request.AdminManagerSearchRequestDto;
import com.homeaid.user.service.AdminManagerService;
import com.homeaid.util.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
@PreAuthorize("hasRole('ADMIN')")
public class AdminManagerController {

  private final AdminManagerService adminManagerService;
  private final S3Service s3Service;
  private final ManagerDocumentRepository documentRepository;


  @GetMapping
  @Operation(summary = "매니저 목록 조회 (검색 포함)", description = "이름, 전화번호, 경력, 상태로 매니저를 검색합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패")
  })
  public ResponseEntity<CommonApiResponse<PagedResponseDto<ManagerResponseDto>>> getManagers(
      @ModelAttribute AdminManagerSearchRequestDto dto,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Manager> managers = adminManagerService.searchManagers(dto, pageable);

    return ResponseEntity.ok(
        CommonApiResponse.success(
            PagedResponseDto.fromPage(managers, ManagerResponseDto::toDto)
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

  @PatchMapping("/{id}/document-review")
  @Operation(summary = "매니저 경력 파일 승인/반려", description = "관리자가 매니저가 업로드한 파일을 보고 승인하거나 반려합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "파일 검토 완료"),
      @ApiResponse(responseCode = "400", description = "요청 오류"),
      @ApiResponse(responseCode = "404", description = "매니저 없음")
  })
  public ResponseEntity<CommonApiResponse<Void>> reviewDocument(
      @PathVariable Long id,
      @RequestBody @Valid AdminDocumentReviewRequest request
  ) {
    adminManagerService.reviewDocument(id, request);
    return ResponseEntity.ok(CommonApiResponse.success());
  }

  @GetMapping("/{managerId}")
  @Operation(summary = "매니저 상세조회 (기본 정보)")
  public ResponseEntity<CommonApiResponse<ManagerResponseDto>> getManagerDetail(
      @PathVariable Long managerId) {
    ManagerResponseDto dto = adminManagerService.getManagerDetail(managerId);
    return ResponseEntity.ok(CommonApiResponse.success(dto));
  }

  @GetMapping("/{managerId}/documents")
  @Operation(summary = "매니저 서류 목록 조회")
  public ResponseEntity<CommonApiResponse<ManagerDocumentListResponseDto>> getManagerDocuments(
      @PathVariable Long managerId) {
    ManagerDocumentListResponseDto dto = adminManagerService.getManagerDocuments(managerId);
    return ResponseEntity.ok(CommonApiResponse.success(dto));
  }

  // 파일 다운로드
  // ResponseEntity<byte[]>로 바로 전달해야 브라우저가 파일로 인식 가능
  @GetMapping("/{documentId}/download")
  public ResponseEntity<byte[]> downloadDocument(@PathVariable Long documentId) {
    ManagerDocument doc = documentRepository.findById(documentId)
        .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));

    var file = s3Service.downloadFile(doc.getS3Key());

    return ResponseEntity.ok()
        .contentType(org.springframework.http.MediaType.parseMediaType(file.getContentType()))
        .contentLength(file.getContentLength())
        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + doc.getOriginalName() + "\"")
        .body(file.getContent());
  }
}
