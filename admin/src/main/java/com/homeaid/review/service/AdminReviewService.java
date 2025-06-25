package com.homeaid.review.service;

import com.homeaid.review.dto.AdminReviewResponseDto;
import com.homeaid.review.dto.ReviewSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminReviewService {
  // 전체 목록 조회
  Page<AdminReviewResponseDto> getAllReviews(ReviewSearchCondition condition, Pageable pageable);

  // 상세 조회
  AdminReviewResponseDto getReviewDetail(Long reviewId);

  // 삭제
  void deleteReview(Long reviewId);

}
