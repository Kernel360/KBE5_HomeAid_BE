package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Manager;
import com.homeaid.dto.request.ManagerDetailInfoRequestDto;
import com.homeaid.dto.response.ManagerDocumentListResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ErrorCode;
import com.homeaid.security.user.CustomUserDetails;
import com.homeaid.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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


  @Operation(
      summary = "매니저 신분 및 자격 증빙서류 제출",
      description = "매니저의 신분 및 자격을 증빙하기 위한 서류를 제출합니다. (신분증, 범죄경력조회서, 건강검진서)"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "증빙서류 첨부 성공"),
      @ApiResponse(responseCode = "400", description = "필수 첨부 파일 누락"),
      @ApiResponse(responseCode = "404", description = "매니저를 찾을 수 없음")
  })
  @PostMapping(value = "/certifications", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<CommonApiResponse<Void>> uploadCertifications(
      @RequestPart MultipartFile idFile,
      @RequestPart MultipartFile criminalRecordFile,
      @RequestPart MultipartFile healthCertificateFile,
      @AuthenticationPrincipal CustomUserDetails user
  ) throws IOException {

    if (idFile.isEmpty() || criminalRecordFile.isEmpty() || healthCertificateFile.isEmpty()) {
      throw new CustomException(ErrorCode.FILE_EMPTY);
    }

    Long managerId = user.getUserId();

    managerService.uploadManagerFiles(managerId, idFile, criminalRecordFile, healthCertificateFile);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(null));
  }


  @Operation(
      summary = "매니저 증빙 서류 조회",
      description = "매니저가 제출한 신분증, 범죄경력조회서, 건강검진서 등의 증빙 서류를 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "증빙서류 조회 성공"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
      @ApiResponse(responseCode = "404", description = "매니저 정보를 찾을 수 없음")
  })
  @GetMapping(value = "/certifications")
  public ResponseEntity<CommonApiResponse<ManagerDocumentListResponseDto>> getUploadCertifications(
      @AuthenticationPrincipal CustomUserDetails user
  ) {

    Long managerId = user.getUserId();

    Manager manager = managerService.getUploadManagerFiles(managerId);
    ManagerDocumentListResponseDto documentsList = ManagerDocumentListResponseDto.toDto(manager);

    return ResponseEntity.status(HttpStatus.OK)
        .body(CommonApiResponse.success(documentsList));
  }
}
