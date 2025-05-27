package com.homeaid.dto.request;


import com.homeaid.domain.ManagerBoard;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateManagerBoardRequestDto {

  @NotBlank(message = "제목을 작성해주세요")
  private String title;

  @NotBlank(message = "내용을 작성해주세요")
  private String content;
  private Boolean isPublic;

  public ManagerBoard toEntity() {
    return ManagerBoard.builder()
        .title(this.title)
        .content(this.content)
        .isPublic(this.isPublic)
        .build();
  }
}
