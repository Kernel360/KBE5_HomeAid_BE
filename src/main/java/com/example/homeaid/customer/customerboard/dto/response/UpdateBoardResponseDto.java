package com.example.homeaid.customer.customerboard.dto.response;

import com.example.homeaid.customer.customerboard.entity.CustomerBoard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBoardResponseDto {

  private Long id;
  private String title;
  private String content;

  public static UpdateBoardResponseDto toDto(CustomerBoard customerBoard) {
    return UpdateBoardResponseDto.builder()
        .id(customerBoard.getId())
        .title(customerBoard.getTitle())
        .content(customerBoard.getContent())
        .build();
  }

}
