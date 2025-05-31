package com.homeaid.dto.request;


import com.homeaid.domain.UserBoard;
import com.homeaid.domain.enumerate.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBoardRequestDto {

  @NotBlank(message = "제목을 작성해 주세요")
  private String title;

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
