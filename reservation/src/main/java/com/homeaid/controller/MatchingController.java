package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.common.response.PagedResponseDto;
import com.homeaid.domain.Matching;
import com.homeaid.dto.request.CreateMatchingRequestDto;
import com.homeaid.dto.request.MatchingCustomerResponseDto;
import com.homeaid.dto.request.MatchingManagerResponseDto;
import com.homeaid.dto.response.ManagerMatchingResponseDto;
import com.homeaid.dto.response.MatchingRecommendationResponseDto;
import com.homeaid.dto.response.MatchingResponseDto;
import com.homeaid.auth.user.CustomUserDetails;
import com.homeaid.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Matching", description = "매칭 관리 API")
public class MatchingController {

  private final MatchingService matchingService;

  @PostMapping("/admin/matchings")
  @Operation(summary = "매칭 생성", description = "관리자가 예약과 매니저 정보를 기반으로 매칭을 생성합니다.")
  public ResponseEntity<CommonApiResponse<Void>> createMatching(
      @Valid @RequestBody CreateMatchingRequestDto matchingRequestDto) {

    matchingService.createMatching(matchingRequestDto.getManagerId(),
        matchingRequestDto.getReservationId());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success());
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

  // 매니저 담당 매칭 전체 조회
  @GetMapping("/manager/matchings")
  @Operation(summary = "매니저 담당 매칭 전체 조회", description = "매니저가 매칭 내역을 페이지네이션으로 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "매칭 리스트 조회 성공",
          content = @Content(schema = @Schema(implementation = MatchingResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "403", description = "조회 권한이 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "해당 매니저의 매칭 존재하지 않음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<PagedResponseDto<MatchingResponseDto>>> getMatchingByManager(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size
  ) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
    Page<Matching> matching = matchingService.getMatchingListByManager(
        userDetails.getUserId(), pageable);

    PagedResponseDto<MatchingResponseDto> response =
        PagedResponseDto.fromPage(matching, MatchingResponseDto::toDto);

    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  // 매니저 담당 매칭 상세 조회
  @GetMapping("/manager/matchings/{matchingId}")
  @Operation(summary = "매니저 매칭 단건 조회", description = "매니저 매칭 단건 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "매니저 매칭 단건 조회 성공",
          content = @Content(schema = @Schema(implementation = MatchingResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "403", description = "조회 권한이 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "해당 매니저의 매칭 존재하지 않음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<ManagerMatchingResponseDto>> getMatching(
      @Parameter(description = "조회할 매칭 ID", example = "1")
      @PathVariable(name = "matchingId") Long matchingId,
      @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    Long userId = customUserDetails.getUserId();
    return ResponseEntity.ok(
        CommonApiResponse.success(
            ManagerMatchingResponseDto.toDto(matchingService.getMatchingByManager(matchingId, userId))));
  }


}
