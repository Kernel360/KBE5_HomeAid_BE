package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.dto.request.CreateMatchingRequestDto;
import com.homeaid.dto.request.MatchingCustomerResponseDto;
import com.homeaid.dto.request.MatchingManagerResponseDto;
import com.homeaid.dto.response.MatchingRecommendationResponseDto;
import com.homeaid.dto.response.ReservationResponseDto;
import com.homeaid.security.CustomUserDetails;
import com.homeaid.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Admin Matching", description = "관리자 매칭 관리 API")
public class MatchingController {

  private final MatchingService matchingService;

  @PostMapping("/admin/matchings")
  @Operation(summary = "매칭 생성", description = "관리자가 예약과 매니저 정보를 기반으로 매칭을 생성합니다.")
  public ResponseEntity<CommonApiResponse<Long>> createMatching(
      @Valid @RequestBody CreateMatchingRequestDto matchingRequestDto) {

    Long matchingId = matchingService.createMatching(matchingRequestDto.getManagerId(),
        matchingRequestDto.getReservationId(), CreateMatchingRequestDto.toEntity());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(matchingId));
  }

  @PatchMapping("/manager/matchings/{matchingId}/to-customer")
  @Operation(summary = "매니저 매칭 응답", description = "매니저가 수락 또는 거절로 매칭에 응답합니다.")
  public ResponseEntity<CommonApiResponse<Void>> respondToMatching(
      @AuthenticationPrincipal CustomUserDetails user,
      @Parameter(description = "매칭 ID", required = true)
      @PathVariable(name = "matchingId") Long matchingId,
      @RequestBody @Valid MatchingManagerResponseDto requestDto
  ) {
    matchingService.respondToMatchingAsManager(user.getUserId(), matchingId, requestDto.getAction(),
        requestDto.getMemo());

    return ResponseEntity.ok().body(CommonApiResponse.success());
  }

  @PatchMapping("/customer/matchings/{matchingId}/to-manager")
  @Operation(summary = "고객 매칭 응답", description = "고객이 수락 또는 거절로 매칭에 응답합니다.")
  public ResponseEntity<CommonApiResponse<Void>> respondToMatching(
      @AuthenticationPrincipal CustomUserDetails user,
      @Parameter(description = "매칭 ID", required = true)
      @PathVariable(name = "matchingId") Long matchingId,
      @RequestBody @Valid MatchingCustomerResponseDto requestDto
  ) {

    matchingService.respondToMatchingAsCustomer(user.getUserId(), matchingId, requestDto.getAction(),
        requestDto.getMemo());

    return ResponseEntity.ok().body(CommonApiResponse.success());
  }

  @PostMapping("/admin/matchings/{reservationId}/recommendations")
  @Operation(summary = "예약 기반 매니저 추천", description = "예약 정보를 기반으로 가능한 매니저 10명을 추천합니다.")
  @ApiResponse(responseCode = "200", description = "매니저 추천 성공",
      content = @Content(schema = @Schema(implementation = MatchingRecommendationResponseDto.class)))
  public ResponseEntity<CommonApiResponse<List<MatchingRecommendationResponseDto>>> recommendManagers(
      @Parameter(description = "예약 ID", required = true) @PathVariable(name = "reservationId") Long reservationId
  ) {

    // Todo: 추후 거리 순, 리뷰 순 등의 정렬 기준 추가

    return ResponseEntity.ok().body(CommonApiResponse.success(
        MatchingRecommendationResponseDto.toDto(
            matchingService.recommendManagers(reservationId))));
  }

}
