package com.example.homeaid.adminreply.dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminReplyUpdateRequestDto {

  @NotBlank(message = "내용은 필수입니다.")
  private String content;

}
