package com.example.homeaid.customerboard.dto.request;

import com.example.homeaid.customerboard.entity.CustomerBoard;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
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

    public static CustomerBoard toEntity(CreateBoardRequestDto createBoardRequestDto) {
        return CustomerBoard.builder()
            .title(createBoardRequestDto.getTitle())
            .content(createBoardRequestDto.getContent())
            .createdAt(LocalDateTime.now())
            .build();
    }

}
