package com.homeaid.dto.response;

import com.homeaid.domain.Review;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TargetReviewResponseDto {

  private Long id;
  private Long writerId;
  private String name;
  private int rating;     //별점
  private String comment;
  private LocalDateTime createdAt;
  private Long reservationId;

  public static TargetReviewResponseDto from(Review review) {
    return TargetReviewResponseDto.builder()
        .id(review.getId())
        .writerId(review.getWriterId())
        .rating(review.getRating())
        .comment(review.getComment())
        .createdAt(review.getCreatedAt())
        .reservationId(review.getReservationId())
        .build();
  }
}
