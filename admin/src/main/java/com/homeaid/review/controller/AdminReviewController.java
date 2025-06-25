package com.homeaid.review.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.review.dto.AdminReviewResponseDto;
import com.homeaid.review.dto.ReviewSearchCondition;
import com.homeaid.review.service.AdminReviewService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/reviews")
@PreAuthorize("hasRole('ADMIN')") // 관리자만 접근
public class AdminReviewController {

  private final AdminReviewService adminReviewService;

  // 전체 리뷰 목록 조회 (검색 조건: 역할, 작성자 ID, 대상자 ID, 작성일 기간)
  @GetMapping
  public ResponseEntity<CommonApiResponse<Page<AdminReviewResponseDto>>> getReviews(
      @ParameterObject ReviewSearchCondition condition,
      @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    return ResponseEntity.ok(
        CommonApiResponse.success(adminReviewService.getAllReviews(condition, pageable))
    );
  }

  // 리뷰 상세 조회
  @GetMapping("/{reviewId}")
  public ResponseEntity<CommonApiResponse<AdminReviewResponseDto>> getReviewDetail(
      @PathVariable Long reviewId
  ) {
    return ResponseEntity.ok(
        CommonApiResponse.success(adminReviewService.getReviewDetail(reviewId))
    );
  }

  // 리뷰 삭제
  @DeleteMapping("/{reviewId}")
  public ResponseEntity<CommonApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
    adminReviewService.deleteReview(reviewId);
    return ResponseEntity.ok(CommonApiResponse.success());
  }
}
