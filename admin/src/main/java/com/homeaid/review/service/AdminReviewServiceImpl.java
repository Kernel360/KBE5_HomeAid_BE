package com.homeaid.review.service;

import com.homeaid.domain.Review;
import com.homeaid.domain.User;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ReviewErrorCode;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.ReviewRepository;
import com.homeaid.repository.UserRepository;
import com.homeaid.review.dto.AdminReviewResponseDto;
import com.homeaid.review.dto.ReviewSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminReviewServiceImpl implements AdminReviewService {

  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;

  @Override
  public Page<AdminReviewResponseDto> getAllReviews(ReviewSearchCondition condition, Pageable pageable) {
    return reviewRepository.findByWriterRoleCondition(condition.getWriterRole(), pageable)
        .map(review -> {
          // 리뷰 작성자 이름과 대상자 이름 조회 후 DTO로 매핑
          User writer = userRepository.findById(review.getWriterId())
              .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
          User target = userRepository.findById(review.getTargetId())
              .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

          return AdminReviewResponseDto.from(review, writer.getName(), target.getName());
        });
  }

  @Override
  public AdminReviewResponseDto getReviewDetail(Long reviewId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    User writer = userRepository.findById(review.getWriterId())
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    User target = userRepository.findById(review.getTargetId())
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    return AdminReviewResponseDto.from(review, writer.getName(), target.getName());
  }

  @Override
  public void deleteReview(Long reviewId) {
    if (!reviewRepository.existsById(reviewId)) {
      throw new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND);
    }
    reviewRepository.deleteById(reviewId);
  }
}
