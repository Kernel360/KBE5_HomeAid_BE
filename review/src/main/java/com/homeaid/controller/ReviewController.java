package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Review;
import com.homeaid.dto.request.CustomerReviewRequestDto;
import com.homeaid.security.CustomUserDetails;
import com.homeaid.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 생성")
    @PostMapping
    public ResponseEntity<CommonApiResponse<Long>> review(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid CustomerReviewRequestDto customerReviewRequestDto) {
        //Todo : uesrDetails에서 작성자 id가지고 와서 dto에 주입

        //하나로 할라면
        Review review = reviewService.save(CustomerReviewRequestDto.toEntity(customerReviewRequestDto, user.getUserRole(), user.getUserId()));

        return ResponseEntity.ok(CommonApiResponse.success(review.getId()));

    }


}
