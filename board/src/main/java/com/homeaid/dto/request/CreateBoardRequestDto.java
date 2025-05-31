package com.homeaid.dto.request;


import com.homeaid.domain.UserBoard;
import com.homeaid.domain.enumerate.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 생성 요청 DTO")
public class CreateBoardRequestDto {

  @Schema(description = "게시글 제목", example = "예약 관련 문의드립니다.", required = true)
  @NotBlank(message = "제목을 작성해 주세요")
  private String title;

  @Schema(description = "게시글 내용", example = "예약 관련해서 문의드립니다.", required = true)
  @NotBlank(message = "내용을 작성해 주세요")
  private String content;

  public static UserBoard toEntity(Long userId, UserRole role,
      CreateBoardRequestDto createBoardRequestDto) {
    return UserBoard.builder()
        .userId(userId)
        .title(createBoardRequestDto.getTitle())
        .content(createBoardRequestDto.getContent())
        .role(role)
        .isAnswered(false)
        .build();
  }

}
