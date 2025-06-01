package com.homeaid.dto.request;


import com.homeaid.domain.Review;
import jakarta.validation.constraints.Size;


public class CustomerReviewRequestDto {

    private Long writerId;

    private Long mangerId;  //매니저아이디

    @Size(min = 1, max = 5)
    private int rating;

    private String comment;


    private Long targetId;
    private Long reservationId;

    public static Review toEntity(CustomerReviewRequestDto customerReviewRequestDto) {
        return Review.builder()
                .comment(customerReviewRequestDto.comment)
                .rating(customerReviewRequestDto.rating)
                .writerId(customerReviewRequestDto.writerId)
                .targetId(customerReviewRequestDto.targetId)
                .build();
    }
}
