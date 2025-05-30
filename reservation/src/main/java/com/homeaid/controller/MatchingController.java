package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.dto.request.CreateMatchingRequestDto;
import com.homeaid.dto.request.MatchingCustomerResponseDto;
import com.homeaid.dto.request.MatchingManagerResponseDto;
import com.homeaid.dto.response.MatchingRecommendationResponseDto;
import com.homeaid.service.MatchingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/matchings")
@RequiredArgsConstructor
public class MatchingController {

  private final MatchingService matchingService;

  @PostMapping
  public ResponseEntity<CommonApiResponse<Long>> createMatching(
      @Valid @RequestBody CreateMatchingRequestDto matchingRequestDto) {

    Long matchingId = matchingService.createMatching(matchingRequestDto.getManagerId(),
        matchingRequestDto.getReservationId(), CreateMatchingRequestDto.toEntity());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(matchingId));
  }

  @PatchMapping("/{matchingId}/to-customer")
  public ResponseEntity<CommonApiResponse<Void>> respondToMatching(
      @PathVariable(name = "matchingId") Long matchingId,
      @RequestBody @Valid MatchingManagerResponseDto requestDto
  ) {
    matchingService.respondToMatchingAsManager(matchingId, requestDto.getAction(),
        requestDto.getMemo());

    return ResponseEntity.ok().body(CommonApiResponse.success());
  }

  @PatchMapping("/{matchingId}/to-manager")
  public ResponseEntity<CommonApiResponse<Void>> respondToMatching(
      @PathVariable(name = "matchingId") Long matchingId,
      @RequestBody @Valid MatchingCustomerResponseDto requestDto
  ) {
    matchingService.respondToMatchingAsCustomer(matchingId, requestDto.getAction(),
        requestDto.getMemo());

    return ResponseEntity.ok().body(CommonApiResponse.success());
  }

  @PostMapping("/{reservationId}/recommendations")
  public ResponseEntity<CommonApiResponse<List<MatchingRecommendationResponseDto>>> recommendManagers(
      @PathVariable Long reservationId
  ) {

    // Todo: 추후 거리 순, 리뷰 순 등의 정렬 기준 추가

    return ResponseEntity.ok().body(CommonApiResponse.success(
        MatchingRecommendationResponseDto.toDto(
            matchingService.recommendManagers(reservationId))));
  }

}
