package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Review;
import com.homeaid.dto.request.CustomerReviewRequestDto;
import com.homeaid.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<CommonApiResponse<Long>> review(@RequestBody @Valid CustomerReviewRequestDto customerReviewRequestDto) {

        //Todo : uesrDetails에서 작성자 id가지고 와서 dto에 주입
        Review review = reviewService.save(CustomerReviewRequestDto.toEntity(customerReviewRequestDto));

        return ResponseEntity.ok(CommonApiResponse.success(review.getId()));

    }


}
