package com.homeaid.dto.response;

import com.homeaid.domain.Review;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WriterReviewResponseDto {

  private Long id;
  private Long targetId;
  private String name;
  private int rating;     //별점
  private String comment;
  private LocalDateTime createdAt;
  private Long reservationId;

  public static WriterReviewResponseDto from(Review review) {
    return WriterReviewResponseDto.builder()
        .id(review.getId())
        .targetId(review.getTargetId())
        .rating(review.getRating())
        .comment(review.getComment())
        .createdAt(review.getCreatedAt())
        .reservationId(review.getReservationId())
        .build();
  }
}
