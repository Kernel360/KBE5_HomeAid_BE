package com.homeaid.controller;



import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Reservation;
import com.homeaid.dto.request.ReservationRequestDto;
import com.homeaid.dto.response.ReservationResponseDto;
import com.homeaid.service.ReservationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer/reservations")
@Tag(name = "Reservation", description = "고객 예약 관련 API")
public class ReservationController {

  private final ReservationServiceImpl reservationService;

  @PostMapping
  @Operation(summary = "예약 생성", description = "고객이 예약 옵션을 선택하여 예약을 생성합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "예약 생성 성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 요청")
  })
  public ResponseEntity<CommonApiResponse<ReservationResponseDto>> createReservation(
      @RequestBody @Valid ReservationRequestDto reservationRequestDto) {

    Reservation reservation = reservationService.createReservation(
        ReservationRequestDto.toEntity(reservationRequestDto),
        reservationRequestDto.getSubOptionId());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(ReservationResponseDto.toDto(reservation)));
  }

  @GetMapping("/id/{reservationId}")
  @Operation(summary = "예약 단건 조회", description = "예약 ID를 기반으로 예약 상세 정보를 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "예약 조회 성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "해당 예약이 존재하지 않음")
  })
  public ResponseEntity<CommonApiResponse<ReservationResponseDto>> getReservation(
      @PathVariable(name = "reservationId") Long reservationId
  ) {
    Reservation reservation = reservationService.getReservation(reservationId);
    return ResponseEntity.ok(CommonApiResponse.success(ReservationResponseDto.toDto(reservation)));
  }

  @PutMapping("/{reservationId}")
  @Operation(summary = "예약 수정", description = "예약 ID에 해당하는 예약 정보를 수정합니다. 단, 상태가 REQUESTED인 경우에만 수정이 가능합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "예약 수정 성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 또는 수정 불가 상태")
  })
  public ResponseEntity<CommonApiResponse<ReservationResponseDto>> updateReservation(
      @PathVariable(name = "reservationId") Long id,
      @RequestBody @Valid ReservationRequestDto reservationRequestDto) {

    Reservation updated = reservationService.updateReservation(
        id,
        ReservationRequestDto.toEntity(reservationRequestDto),
        reservationRequestDto.getSubOptionId());

    return ResponseEntity.ok(CommonApiResponse.success(ReservationResponseDto.toDto(updated)));
  }

  @DeleteMapping("/{reservationId}")
  @Operation(summary = "예약 삭제", description = "예약 ID에 해당하는 예약을 삭제합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "예약 삭제 성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "예약이 존재하지 않음")
  })
  public ResponseEntity<CommonApiResponse<Void>> deleteReservation(
      @PathVariable(name = "reservationId") Long id
  ) {
    reservationService.deleteReservation(id);
    return ResponseEntity.ok(CommonApiResponse.success(null));
  }
}
