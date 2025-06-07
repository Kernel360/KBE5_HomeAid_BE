package com.homeaid.controller;


import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.common.response.PagedResponseDto;
import com.homeaid.domain.Reservation;
import com.homeaid.dto.request.ReservationRequestDto;
import com.homeaid.dto.response.ReservationResponseDto;
import com.homeaid.security.CustomUserDetails;
import com.homeaid.service.ReservationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation", description = "예약 관련 API")
public class ReservationController {

  private final ReservationServiceImpl reservationService;

  @PostMapping
  @Operation(summary = "예약 생성", description = "고객이 예약 옵션을 선택하여 예약을 생성합니다.")
  @ApiResponse(responseCode = "201", description = "예약 생성 성공",
      content = @Content(schema = @Schema(implementation = ReservationResponseDto.class)))
  @ApiResponse(responseCode = "400", description = "유효하지 않은 요청",
      content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  public ResponseEntity<CommonApiResponse<ReservationResponseDto>> createReservation(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestBody @Valid ReservationRequestDto reservationRequestDto) {

    Reservation reservation = reservationService.createReservation(
        ReservationRequestDto.toEntity(reservationRequestDto, user.getUserId()),
        reservationRequestDto.getSubOptionId());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(ReservationResponseDto.toDto(reservation)));
  }

  @GetMapping("/{reservationId}")
  @Operation(summary = "예약 단건 조회", description = "예약 ID를 기반으로 예약 상세 정보를 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "예약 조회 성공",
          content = @Content(schema = @Schema(implementation = ReservationResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "해당 예약이 존재하지 않음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
  })
  public ResponseEntity<CommonApiResponse<ReservationResponseDto>> getReservation(
      @Parameter(description = "조회할 예약 ID", example = "1")
      @PathVariable(name = "reservationId") Long reservationId
  ) {
    Reservation reservation = reservationService.getReservation(reservationId);
    return ResponseEntity.ok(CommonApiResponse.success(ReservationResponseDto.toDto(reservation)));
  }

  @PutMapping("/{reservationId}")
  @Operation(summary = "예약 수정", description = "예약 ID에 해당하는 예약 정보를 수정합니다. 단, 상태가 REQUESTED인 경우에만 수정이 가능합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "예약 수정 성공",
          content = @Content(schema = @Schema(implementation = ReservationResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 또는 수정 불가 상태",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
  })
  public ResponseEntity<CommonApiResponse<ReservationResponseDto>> updateReservation(
      @AuthenticationPrincipal CustomUserDetails user,
      @Parameter(description = "수정할 예약 ID", example = "1")
      @PathVariable(name = "reservationId") Long reservationId,
      @RequestBody @Valid ReservationRequestDto reservationRequestDto) {

    Reservation updated = reservationService.updateReservation(
        user.getUserId(),
        reservationId,
        ReservationRequestDto.toEntity(reservationRequestDto),
        reservationRequestDto.getSubOptionId());

    return ResponseEntity.ok(CommonApiResponse.success(ReservationResponseDto.toDto(updated)));
  }

  @DeleteMapping("/{reservationId}")
  @Operation(summary = "예약 삭제", description = "예약 ID에 해당하는 예약을 삭제합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "예약 삭제 성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "예약이 존재하지 않음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
  })
  public ResponseEntity<CommonApiResponse<Void>> deleteReservation(
      @AuthenticationPrincipal CustomUserDetails user,
      @Parameter(description = "삭제할 예약 ID", example = "1")
      @PathVariable(name = "reservationId") Long reservationId
  ) {
    reservationService.deleteReservation(reservationId, user.getUserId());
    return ResponseEntity.ok(CommonApiResponse.success(null));
  }

  /**
   * 관리자용 예약 전체 조회
   */
  @GetMapping
  @Operation(summary = "예약 전체 조회", description = "페이지네이션 기반으로 예약 목록을 조회합니다.")
  public ResponseEntity<CommonApiResponse<PagedResponseDto<ReservationResponseDto>>> getReservationsList(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

    Page<Reservation> reservations = reservationService.getReservations(pageable);

    PagedResponseDto<ReservationResponseDto> response = PagedResponseDto.fromPage(reservations,
        ReservationResponseDto::toDto);
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  /**
   * 로그인한 고객의 예약 전체 조회
   */
  @GetMapping("/customer")
  @Operation(summary = "특정 유저의 예약 전체 조회", description = "고객의 모든 예약을 페이지네이션으로 조회합니다.")
  public ResponseEntity<CommonApiResponse<PagedResponseDto<ReservationResponseDto>>> getReservationsByCustomer(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
    Page<Reservation> reservations = reservationService.getReservationsByCustomer(user.getUserId(),
        pageable);

    PagedResponseDto<ReservationResponseDto> response =
        PagedResponseDto.fromPage(reservations, ReservationResponseDto::toDto);
    return ResponseEntity.ok(CommonApiResponse.success(response));
  }

  /**
   * 매니저 담당 예약 전체 조회
   */
  @GetMapping("/manager")
  @Operation(summary = "매니저 담당 예약 전체 조회", description = "매니저가 담당 중인 예약을 페이지네이션으로 조회합니다.")
  public ResponseEntity<CommonApiResponse<PagedResponseDto<ReservationResponseDto>>> getReservationsByManager(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size
  ) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
    Page<Reservation> reservations = reservationService.getReservationsByManager(
        userDetails.getUserId(), pageable);

    PagedResponseDto<ReservationResponseDto> response =
        PagedResponseDto.fromPage(reservations, ReservationResponseDto::toDto);

    return ResponseEntity.ok(CommonApiResponse.success(response));
  }


}
