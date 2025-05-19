package com.example.homeaid.customerboard.dto.response;

import com.example.homeaid.customerboard.entity.CustomerBoard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBoardResponseDto {

    private String title;
    private String content;

    public static CreateBoardResponseDto toDto(CustomerBoard customerBoard) {
        return CreateBoardResponseDto.builder().title(customerBoard.getTitle())
            .content(customerBoard.getContent()).build();
    }

}
