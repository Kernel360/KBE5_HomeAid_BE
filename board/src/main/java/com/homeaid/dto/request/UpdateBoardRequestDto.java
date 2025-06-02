package com.homeaid.dto.request;


import com.homeaid.domain.UserBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "게시글 수정 요청 DTO")
public class UpdateBoardRequestDto {

  @Schema(description = "수정할 게시글 제목", example = "수정된 제목입니다", required = true)
  @NotBlank(message = "제목을 작성해 주세요")
  private String title;

  @Schema(description = "수정할 게시글 내용", example = "수정된 내용입니다.", required = true)
  @NotBlank(message = "내용을 작성해 주세요")
  private String content;
  public static UserBoard toEntity(UpdateBoardRequestDto updateBoardRequestDto) {
    return UserBoard.builder()
        .title(updateBoardRequestDto.getTitle())
        .content(updateBoardRequestDto.getContent())
        .build();
  }

}
