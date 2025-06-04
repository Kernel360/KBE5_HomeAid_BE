package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Review;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.request.CustomerReviewRequestDto;
import com.homeaid.security.CustomUserDetails;
import com.homeaid.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 생성")
    @PostMapping
    public ResponseEntity<CommonApiResponse<Long>> createReview(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CustomerReviewRequestDto customerReviewRequestDto) {

        Review requestReview = CustomerReviewRequestDto.toEntity(customerReviewRequestDto, user.getUserRole(), user.getUserId());

        Review review = switch (user.getUserRole()) {
            case CUSTOMER -> reviewService.createReviewByCustomer(requestReview);
            case MANAGER -> reviewService.createReviewByManager(requestReview);
            default -> throw new IllegalArgumentException("Unsupported role: " + user.getUserRole());
        };
        return ResponseEntity.ok(CommonApiResponse.success(review.getId()));
    }

    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<CommonApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId) {

        reviewService.deleteReview(reviewId, userDetails.getUserId());
        return ResponseEntity.ok(CommonApiResponse.success());
    }


}
