package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Review;
import com.homeaid.dto.request.CustomerReviewRequestDto;
import com.homeaid.dto.response.MyReviewResponseDto;
import com.homeaid.paging.PagingResponseDto;
import com.homeaid.paging.PagingResponseUtil;
import com.homeaid.security.CustomUserDetails;
import com.homeaid.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @Operation(summary = "한 리뷰어의 리뷰 리스트", description = "본인리뷰, 상대리뷰 통합")
    @GetMapping("/{reviewer}")
    public ResponseEntity<CommonApiResponse<PagingResponseDto<MyReviewResponseDto>>> getReviewOfWriter(
            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable Long reviewer) {
        Page<MyReviewResponseDto> myReviewResponseDtoPage = reviewService.getReviewOfWriter(reviewer, pageable)
                .map(MyReviewResponseDto::from);

        return ResponseEntity.ok(CommonApiResponse.success(PagingResponseUtil.newInstance(myReviewResponseDtoPage)));
    }



}
