package com.homeaid.dto.request;


import com.homeaid.domain.Review;
import com.homeaid.domain.enumerate.UserRole;
import jakarta.validation.constraints.Size;
import lombok.Getter;


@Getter
public class CustomerReviewRequestDto {

    private Long targetId;

    private UserRole writerRole;


    @Size(min = 1, max = 5)
    private int rating;

    private String comment;

    private Long reservationId;

    public static Review toEntity(CustomerReviewRequestDto customerReviewRequestDto, UserRole userRole, Long userId) {

        return Review.builder()
                .writerId(userId)
                .targetId(customerReviewRequestDto.targetId)
                .writerRole(userRole)
                .comment(customerReviewRequestDto.comment)
                .rating(customerReviewRequestDto.rating)
                .build();
    }
}
