package com.example.homeaid.customerboard.dto.request;

import com.example.homeaid.customerboard.entity.CustomerBoard;
import lombok.Getter;

@Getter
public class CreateBoardRequestDto {

    private String title;
    private String content;

    public static CustomerBoard toEntity(CreateBoardRequestDto createBoardRequestDto) {
        return CustomerBoard.builder()
            .title(createBoardRequestDto.getTitle())
            .content(createBoardRequestDto.getContent())
            .build();
    }

}
