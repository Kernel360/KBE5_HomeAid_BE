package com.homeaid.dto.response;

import com.homeaid.domain.Review;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class MyReviewResponseDto {
    private Long id;
    private Long targetId;
    private int rating;     //별점
    private String comment;
    private LocalDateTime createdAt;
    private Long reservationId;

    public static MyReviewResponseDto from(Review review) {
        return MyReviewResponseDto.builder()
                .id(review.getId())
                .targetId(review.getTargetId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .reservationId(review.getReservationId())
                .build();
    }
}
