package com.homeaid.service;


import com.homeaid.domain.Review;

public interface ReviewService {
    Review createReviewByCustomer(Review requestReview);

    Review createReviewByManager(Review requestReview);

    void deleteReview(Long reviewId, Long userId);
}
