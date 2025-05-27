package com.homeaid.dto.response;


import com.homeaid.domain.CustomerBoard;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBoardResponseDto {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public static CreateBoardResponseDto toDto(CustomerBoard customerBoard) {
        return CreateBoardResponseDto.builder()
            .id(customerBoard.getId())
            .title(customerBoard.getTitle())
            .content(customerBoard.getContent())
            .createdAt(customerBoard.getCreatedAt())
            .build();
    }

}
