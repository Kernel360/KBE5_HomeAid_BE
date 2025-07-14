package com.homeaid.service;


import com.homeaid.domain.Review;
import com.homeaid.domain.enumerate.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReviewService {

  Review createReviewByCustomer(Review requestReview);

  Review createReviewByManager(Review requestReview);

  void deleteReview(Long reviewId, Long userId);

  Page<Review> getReviewOfWriter(Long writerId, Pageable pageable);

  Page<Review> getReviewOfTarget(Long targetId, Pageable pageable);

  Long getReviewTargetInfo(Long reservationId, Long userId, UserRole userRole);
}
