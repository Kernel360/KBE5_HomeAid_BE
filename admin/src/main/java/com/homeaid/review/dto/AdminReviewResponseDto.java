package com.homeaid.review.dto;

import com.homeaid.domain.Review;
import com.homeaid.domain.enumerate.UserRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminReviewResponseDto {
  private Long id;
  private Long writerId;
  private String writerName;     // 👈 작성자 이름
  private Long targetId;
  private String targetName;     // 👈 대상자 이름
  private UserRole writerRole;
  private int rating;
  private String comment;
  private LocalDateTime createdAt;
  private Long reservationId;

  // 전체 정보를 받아서 DTO로 변환
  public static AdminReviewResponseDto from(Review review, String writerName, String targetName) {
    return AdminReviewResponseDto.builder()
        .id(review.getId())
        .writerId(review.getWriterId())
        .writerName(writerName)
        .targetId(review.getTargetId())
        .targetName(targetName)
        .writerRole(review.getWriterRole())
        .rating(review.getRating())
        .comment(review.getComment())
        .createdAt(review.getCreatedAt())
        .reservationId(review.getReservationId())
        .build();
  }
}
