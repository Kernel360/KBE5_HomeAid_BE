package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.UserBoard;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.request.CreateBoardRequestDto;
import com.homeaid.dto.request.UpdateBoardRequestDto;
import com.homeaid.common.response.PagedResponseDto;
import com.homeaid.dto.response.UserBoardListResponseDto;
import com.homeaid.dto.response.UserBoardResponseDto;
import com.homeaid.security.user.CustomUserDetails;
import com.homeaid.service.UserBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
@Tag(name = "1:1 문의글", description = "사용자와 관리자 간 1:1 문의글 API")
@SecurityRequirement(name = "Bearer Authentication")
public class UserBoardController {

  private final UserBoardService userBoardService;

  @PostMapping("")
  @Operation(summary = "1:1 문의글 작성", description = "고객과 매니저의 권한을 구분하여 관리자에게 1:1 문의글을 작성합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "문의글 작성 성공",
          content = @Content(schema = @Schema(implementation = UserBoardResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 요청",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<UserBoardResponseDto>> createBoard(
      @RequestBody @Valid CreateBoardRequestDto createRequestDto,
      @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    Long userId = customUserDetails.getUserId();
    UserRole role = customUserDetails.getUserRole();
    UserBoard board = CreateBoardRequestDto.toEntity(userId, role, createRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(
            UserBoardResponseDto.toDto(userBoardService.createBoard(board))));
  }

  @PutMapping("/{boardId}")
  @Operation(summary = "1:1 문의글 수정", description = "사용자는 본인이 작성한 문의글만 수정할 수 있습니다. ")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "문의글 수정 성공",
          content = @Content(schema = @Schema(implementation = UserBoardResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "403", description = "수정 권한이 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "해당 문의글 존재하지 않음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<UserBoardResponseDto>> updateBoard(
      @Parameter(description = "수정할 문의글 ID", example = "1")
      @PathVariable(name = "boardId") Long id,
      @RequestBody @Valid UpdateBoardRequestDto updateBoardRequestDto,
      @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    Long userId = customUserDetails.getUserId();

    UserBoard board = userBoardService.updateBoard(id, userId,
        UpdateBoardRequestDto.toEntity(updateBoardRequestDto));

    return ResponseEntity.status(HttpStatus.OK).body(
        CommonApiResponse.success((UserBoardResponseDto.toDto(board)))
    );
  }

  @DeleteMapping("/{boardId}")
  @Operation(summary = "문의글 삭제", description = "사용자는 본인이 작성한 문의글만 삭제할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "문의글 삭제 성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "403", description = "수정 권한이 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "해당 문의글 존재하지 않음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<Void>> deleteBoard(
      @Parameter(description = "삭제할 문의글 ID", example = "1")
      @PathVariable(name = "boardId") Long id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    Long userId = customUserDetails.getUserId();

    userBoardService.deleteBoard(id, userId);
    return ResponseEntity.ok(CommonApiResponse.success(null));
  }

  @GetMapping("/{boardId}")
  @Operation(summary = "문의글 단건 조회", description = "사용자는 본인이 작성한 문의글만 조회할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "문의글 단건 조회 성공",
          content = @Content(schema = @Schema(implementation = UserBoardResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "403", description = "조회 권한이 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "해당 문의글 존재하지 않음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<UserBoardResponseDto>> getBoard(
      @Parameter(description = "조회할 문의글 ID", example = "1")
      @PathVariable(name = "boardId") Long id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    Long userId = customUserDetails.getUserId();
    return ResponseEntity.ok(
        CommonApiResponse.success(
            UserBoardResponseDto.toDto(userBoardService.getBoard(id, userId))));
  }

  @GetMapping("")
  @Operation(summary = "문의글 전체 조회 및 검색", description = "사용자는 본인이 작성한 모든 문의글을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "문의글 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = UserBoardListResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<PagedResponseDto<UserBoardListResponseDto>>> searchBoard(
      @Parameter(description = "검색 키워드")
      @RequestParam(name = "keyword", required = false) String keyword,
      @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
      @RequestParam(name = "page", defaultValue = "0") int page,
      @Parameter(description = "페이지 크기", example = "10")
      @RequestParam(name = "size", defaultValue = "10") int size,
      @Parameter(description = "정렬 기준 필드", example = "createdAt")
      @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
      @Parameter(description = "정렬 방향 (asc, desc)", example = "desc")
      @RequestParam(name = "sortDirection", defaultValue = "desc") String sortDirection,
      @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    Long userId = customUserDetails.getUserId();

    Sort sort = sortDirection.equalsIgnoreCase("desc") ?
        Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

    Pageable pageable = PageRequest.of(page, size, sort);

    return ResponseEntity.ok(
        CommonApiResponse.success(userBoardService.searchBoard(keyword, pageable, userId)));
  }
}
