package com.homeaid.boardreply.controller;

import com.homeaid.boardreply.domain.BoardReply;
import com.homeaid.boardreply.dto.request.BoardReplyUpdateRequestDto;
import com.homeaid.boardreply.service.BoardReplyService;
import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.boardreply.dto.request.BoardReplyCreateRequestDto;
import com.homeaid.boardreply.dto.response.BoardReplyResponseDto;
import com.homeaid.security.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/replies")
@RequiredArgsConstructor
@Tag(name = "관리자 문의글 답변", description = "관리자 문의글 답변 API")
public class BoardReplyController {

  private final BoardReplyService boardReplyService;

  @PostMapping("/{postType}/id/{boardId}")
  @Operation(summary = "[관리자] 답변 작성", responses = {
      @ApiResponse(responseCode = "201", description = "성공",
          content = @Content(schema = @Schema(implementation = BoardReplyResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 요청",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<BoardReplyResponseDto>> createReply(
      @PathVariable(name = "boardId") Long boardId,
      @RequestBody @Valid BoardReplyCreateRequestDto boardReplyCreateRequestDto,
      @AuthenticationPrincipal CustomUserDetails admin
  ) {
    Long adminId = admin.getUserId();
    BoardReply boardReply = BoardReplyCreateRequestDto.toEntity(boardId, adminId,
        boardReplyCreateRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(CommonApiResponse.success(
        BoardReplyResponseDto.toDto(boardReplyService.createReply(boardReply))));
  }

  @PutMapping("/{postType}/id/{boardId}/replyId/{replyId}")
  @Operation(summary = "[관리자] 답변 수정", responses = {
      @ApiResponse(responseCode = "200", description = "성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "해당 게시글 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<BoardReplyResponseDto>> updateReply(
      @PathVariable(name = "boardId") Long boardId,
      @PathVariable(name = "replyId") Long replyId,
      @RequestBody @Valid BoardReplyUpdateRequestDto boardReplyUpdateRequestDto,
      @AuthenticationPrincipal CustomUserDetails admin
  ) {

    Long adminId = admin.getUserId();
    BoardReply updatedReply = boardReplyService.updateReply(boardId, replyId, adminId,
        BoardReplyUpdateRequestDto.toEntity(boardReplyUpdateRequestDto)
    );

    return ResponseEntity.ok(CommonApiResponse.success(
        BoardReplyResponseDto.toDto(updatedReply))
    );
  }


  @DeleteMapping("/{boardId}/replies/{replyId}")
  @Operation(summary = "[관리자] 답변 삭제", responses = {
      @ApiResponse(responseCode = "200", description = "성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "해당 게시글 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<Void>> deleteAnswer(
      @PathVariable(name = "boardId") Long boardId,
      @PathVariable(name = "replyId") Long replyId,
      @AuthenticationPrincipal CustomUserDetails admin
  ) {
    Long adminId = admin.getUserId();
    boardReplyService.deleteReply(boardId, replyId, adminId);
    return ResponseEntity.ok(CommonApiResponse.success());
  }


}