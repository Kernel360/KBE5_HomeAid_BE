package com.homeaid.dto.request;


import com.homeaid.domain.Review;
import com.homeaid.domain.enumerate.UserRole;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CustomerReviewRequestDto {

    @NotNull(message = "리뷰 대상자는 필수입니다")
    private Long targetId;

    @NotNull(message = "평점은 필수입니다")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다")
    private int rating;

    @NotBlank(message = "리뷰 내용은 필수입니다")
    @Size(min = 10, message = "리뷰는 10자 이상 작성해주세요")
    private String comment;

    @NotNull(message = "예약 ID는 필수입니다")
    private Long reservationId;

    public static Review toEntity(CustomerReviewRequestDto customerReviewRequestDto, UserRole userRole, Long userId) {
        return Review.builder()
                .writerId(userId)
                .targetId(customerReviewRequestDto.targetId)
                .writerRole(userRole)
                .comment(customerReviewRequestDto.comment)
                .rating(customerReviewRequestDto.rating)
                .reservationId(customerReviewRequestDto.reservationId)
                .build();
    }
}
