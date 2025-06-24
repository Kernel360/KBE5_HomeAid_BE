package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Review;
import com.homeaid.dto.request.CustomerReviewRequestDto;
import com.homeaid.dto.response.TargetReviewResponseDto;
import com.homeaid.dto.response.WriterReviewResponseDto;
import com.homeaid.paging.PagingResponseDto;
import com.homeaid.paging.PagingResponseUtil;
import com.homeaid.security.user.CustomUserDetails;
import com.homeaid.service.ReviewService;
import com.homeaid.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

  private final ReviewService reviewService;
  private final UserService userService;

  @Operation(summary = "리뷰 생성")
  @PostMapping
  public ResponseEntity<CommonApiResponse<Long>> createReview(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestBody @Valid CustomerReviewRequestDto customerReviewRequestDto) {

    Review requestReview = CustomerReviewRequestDto.toEntity(customerReviewRequestDto,
        user.getUserRole(), user.getUserId());

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

  @Operation(summary = "작성한 리뷰 목록 조회", description = "사용자가 작성한 리뷰 목록을 조회합니다.")
  @GetMapping("/reviewer")
  public ResponseEntity<CommonApiResponse<PagingResponseDto<WriterReviewResponseDto>>> getReviewOfWriter(
      @ParameterObject
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
      @AuthenticationPrincipal CustomUserDetails user) {

    Long userId = user.getUserId();
    Page<WriterReviewResponseDto> myReviewResponseDtoPage = reviewService.getReviewOfWriter(userId,
            pageable)
        .map(WriterReviewResponseDto::from);

    // 리뷰 대상 이름 셋팅하기
    myReviewResponseDtoPage.getContent().forEach(review -> {
      review.setName(userService.getUserById(review.getTargetId()).getName());
    });

    return ResponseEntity.ok(
        CommonApiResponse.success(PagingResponseUtil.newInstance(myReviewResponseDtoPage)));
  }

  @Operation(summary = "매니저 리뷰 목록 조회", description = "매니저에게 작성된 리뷰 목록을 조회합니다.")
  @GetMapping("/{targetId}")
  public ResponseEntity<CommonApiResponse<PagingResponseDto<TargetReviewResponseDto>>> getReviewOfTarget(
      @ParameterObject
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
      @PathVariable Long targetId) {

    Page<TargetReviewResponseDto> targetResponseDtoPage = reviewService.getReviewOfTarget(targetId,
            pageable)
        .map(TargetReviewResponseDto::from);

    // 리뷰 작성자 이름 셋팅하기
    targetResponseDtoPage.getContent().forEach(review -> {
      review.setName(userService.getUserById(review.getWriterId()).getName());
    });

    return ResponseEntity.ok(
        CommonApiResponse.success(PagingResponseUtil.newInstance(targetResponseDtoPage)));
  }
}
