package com.example.homeaid.admin.adminreply.controller;

import com.example.homeaid.admin.adminreply.dto.Request.AdminReplyRequestDto;
import com.example.homeaid.admin.adminreply.dto.Response.AdminReplyResponseDto;
import com.example.homeaid.admin.adminreply.entity.AdminReply;
import com.example.homeaid.admin.adminreply.entity.PostType;
import com.example.homeaid.admin.adminreply.service.AdminReplyService;
import com.example.homeaid.customer.customerboard.dto.response.UpdateBoardResponseDto;
import com.example.homeaid.global.common.response.CommonApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * api/admin/boards/{boardType}/posts/{postId}/reply // post
 * api/admin/boards/{boardType}/replies/{replyId} // put api/admin/boards/{boardType}/replies
 * api/boards/{boardType}/{boardId} // 게시글 답변 조회
 */

@RestController
@RequestMapping("api/admin/posts")
@RequiredArgsConstructor
public class AdminReplyController {

  private final AdminReplyService adminReplyService;

  @PostMapping("/{postType}/id/{postId}")
  @Operation(summary = "[관리자] 답변 작성", responses = {
      @ApiResponse(responseCode = "201", description = "성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<AdminReplyResponseDto>> createReply(
      @PathVariable(name = "postType") PostType postType,
      @PathVariable(name = "postId") Long postId,
      @RequestBody @Valid AdminReplyRequestDto adminReplyRequestDto
  ) {
    AdminReply createdReply = adminReplyService.createReply(
        postType, postId, AdminReplyRequestDto.toEntity(adminReplyRequestDto));

    return ResponseEntity.status(HttpStatus.CREATED).body(CommonApiResponse.success(
        AdminReplyResponseDto.toDto(createdReply)));
  }

  @PutMapping("/{postType}/id/{postId}")
  @Operation(summary = "[관리자] 답변 수정", responses = {
      @ApiResponse(responseCode = "200", description = "성공",
          content = @Content(schema = @Schema(implementation = UpdateBoardResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "해당 게시글 없음",
          content = @Content(schema = @Schema(implementation = UpdateBoardResponseDto.class)))
  })
  public ResponseEntity<CommonApiResponse<AdminReplyResponseDto>> updateReply(
      @PathVariable(name = "postType") PostType postType,
      @PathVariable(name = "postId") Long postId,
      @RequestBody @Valid AdminReplyRequestDto adminReplyRequestDto
  ) {
    AdminReply createdReply = adminReplyService.updateReply(
        postType, postId, adminReplyRequestDto.getContent());

    return ResponseEntity.status(HttpStatus.CREATED).body(CommonApiResponse.success(
        AdminReplyResponseDto.toDto(createdReply)));
  }



@DeleteMapping("/replies/{replyId}")
@Operation(summary = "[관리자] 답변 삭제", responses = {
    @ApiResponse(responseCode = "200", description = "성공",
        content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
    @ApiResponse(responseCode = "404", description = "해당 게시글 없음",
        content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
})
public ResponseEntity<CommonApiResponse<Void>> deleteAnswer(
    @PathVariable(name = "replyId") Long replyId
) {
  adminReplyService.deleteReply(replyId);
  return ResponseEntity.ok(CommonApiResponse.success());
}


}