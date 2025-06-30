package com.homeaid.boardreply.dto.response;

import com.homeaid.dto.response.UserBoardListResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "문의글 + 답변 통합 조회 DTO")
public class InquiryWithReplyResponseDto {

  @Schema(description = "문의글 정보")
  private UserBoardListResponseDto board;

  @Schema(description = "답변 정보 (없을 수 있음)")
  private InquiryDetailResponseDto reply;
}