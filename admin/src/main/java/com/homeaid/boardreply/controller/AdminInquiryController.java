package com.homeaid.boardreply.controller;

import com.homeaid.boardreply.dto.request.BoardReplyCreateRequestDto;
import com.homeaid.boardreply.dto.request.BoardReplyUpdateRequestDto;
import com.homeaid.boardreply.dto.response.BoardReplyListResponseDto;
import com.homeaid.boardreply.dto.response.InquiryDetailResponseDto;
import com.homeaid.boardreply.dto.response.InquiryWithReplyResponseDto;
import com.homeaid.boardreply.service.AdminInquiryService;
import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.common.response.PagedResponseDto;
import com.homeaid.domain.BoardReply;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.response.UserBoardListResponseDto;
import com.homeaid.security.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@RequestMapping("/api/v1/admin/inquiries")
@Tag(name = "관리자 문의글 조회", description = "관리자가 문의글을 조회하고 답변을 작성/수정/삭제하는 API")
public class AdminInquiryController {

  private final AdminInquiryService adminInquiryService;

  // 전체 문의글 목록 조회
  @GetMapping("/boards")
  @Operation(summary = "전체 문의글 조회")
  public ResponseEntity<CommonApiResponse<PagedResponseDto<UserBoardListResponseDto>>> getAllUserBoards(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<UserBoardListResponseDto> result = adminInquiryService.getAllUserBoards(pageable);
    return ResponseEntity.ok(CommonApiResponse.success(PagedResponseDto.fromPage(result)));
  }

  // ✅ 전체 답변 목록 조회 (필터링, 페이징)
  @GetMapping
  @Operation(summary = "전체 답변 목록 조회 (역할, 키워드 필터링 포함)")
  public ResponseEntity<CommonApiResponse<PagedResponseDto<BoardReplyListResponseDto>>> getAllReplies(
      @RequestParam(required = false) UserRole role,
      @RequestParam(required = false, defaultValue = "") String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDirection
  ) {
    Sort sort = sortDirection.equalsIgnoreCase("desc")
        ? Sort.by(sortBy).descending()
        : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<BoardReplyListResponseDto> result = adminInquiryService.getAllReplies(role, keyword, pageable);
    return ResponseEntity.ok(CommonApiResponse.success(PagedResponseDto.fromPage(result)));
  }

  // ✅ 답변 단건 조회
  @GetMapping("/{replyId}")
  @Operation(summary = "단건 답변 상세 조회")
  public ResponseEntity<CommonApiResponse<InquiryDetailResponseDto>> getReplyById(
      @PathVariable Long replyId
  ) {
    InquiryDetailResponseDto response = adminInquiryService.getReplyById(replyId);
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  // ✅ 특정 유저의 답변 목록 조회
  @GetMapping("/user")
  @Operation(summary = "특정 유저의 답변 목록 조회")
  public ResponseEntity<CommonApiResponse<PagedResponseDto<BoardReplyListResponseDto>>> getRepliesByUserId(
      @RequestParam Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDirection
  ) {
    Sort sort = sortDirection.equalsIgnoreCase("desc")
        ? Sort.by(sortBy).descending()
        : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<BoardReplyListResponseDto> result = adminInquiryService.getRepliesByUserId(userId, pageable);
    return ResponseEntity.ok(CommonApiResponse.success(PagedResponseDto.fromPage(result)));
  }

  // ✅ 답변 작성
  @PostMapping("/board/{boardId}")
  @Operation(summary = "[관리자] 답변 작성")
  public ResponseEntity<CommonApiResponse<BoardReplyListResponseDto>> createReply(
      @PathVariable Long boardId,
      @RequestBody @Valid BoardReplyCreateRequestDto dto,
      @AuthenticationPrincipal CustomUserDetails admin
  ) {
    Long adminId = admin.getUserId();
    BoardReply tempReply = BoardReply.builder()
        .boardId(boardId)
        .adminId(adminId)
        .userId(adminId)
        .content(dto.getContent())
        .userRole(UserRole.ADMIN)
        .build();

    BoardReply savedReply = adminInquiryService.createReply(tempReply);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(BoardReplyListResponseDto.toDto(savedReply,
            savedReply.getUser() != null ? savedReply.getUser().getName() : null)));
  }

  // ✅ 답변 수정
  @PutMapping("/board/{boardId}/reply/{replyId}")
  @Operation(summary = "[관리자] 답변 수정")
  public ResponseEntity<CommonApiResponse<InquiryDetailResponseDto>> updateReply(
      @PathVariable Long boardId,
      @PathVariable Long replyId,
      @RequestBody @Valid BoardReplyUpdateRequestDto requestDto,
      @AuthenticationPrincipal CustomUserDetails admin
  ) {
    Long adminId = admin.getUserId();
    BoardReply requestReply = BoardReplyUpdateRequestDto.toEntity(requestDto);
    BoardReply updatedReply = adminInquiryService.updateReply(boardId, replyId, adminId, requestReply);

    String userName = updatedReply.getUser() != null ? updatedReply.getUser().getName() : null;

    return ResponseEntity.ok(CommonApiResponse.success(InquiryDetailResponseDto.from(updatedReply, userName)));
  }

  // ✅ 답변 삭제
  @DeleteMapping("/board/{boardId}/reply/{replyId}")
  @Operation(summary = "[관리자] 답변 삭제")
  public ResponseEntity<CommonApiResponse<Void>> deleteReply(
      @PathVariable Long boardId,
      @PathVariable Long replyId,
      @AuthenticationPrincipal CustomUserDetails admin
  ) {
    Long adminId = admin.getUserId();
    adminInquiryService.deleteReply(boardId, replyId, adminId);
    return ResponseEntity.ok(CommonApiResponse.success());
  }

  @GetMapping("/board/{boardId}/with-reply")
  @Operation(summary = "문의글 + 답변 통합 조회", description = "특정 문의글과 그에 대한 답변을 함께 조회합니다.")
  public ResponseEntity<CommonApiResponse<InquiryWithReplyResponseDto>> getBoardWithReply(
      @PathVariable Long boardId) {
    InquiryWithReplyResponseDto dto = adminInquiryService.getBoardWithReply(boardId);
    return ResponseEntity.ok(CommonApiResponse.success(dto));
  }
}