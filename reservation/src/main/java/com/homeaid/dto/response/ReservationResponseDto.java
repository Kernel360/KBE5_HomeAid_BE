package com.homeaid.dto.response;


import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "예약 응답 DTO")
public class ReservationResponseDto {

  @Schema(description = "예약 ID", example = "101")
  private Long reservationId;

  @Schema(description = "예약 상태", example = "REQUESTED")
  private ReservationStatus status;

  @Schema(description = "총 가격 (단위: 원)", example = "45000")
  private Integer totalPrice;

  @Schema(description = "총 소요 시간 (단위: 분)", example = "120")
  private Integer totalDuration;

  @Schema(description = "예약 서비스 명", example = "대청소")
  private String subOptionName;

  public static ReservationResponseDto toDto(Reservation reservation) {
    return ReservationResponseDto.builder()
        .reservationId(reservation.getId())
        .status(reservation.getStatus())
        .totalPrice(reservation.getTotalPrice())
        .totalDuration(reservation.getTotalDuration())
        .subOptionName(reservation.getItem().getSubOptionName())
        .build();
  }
}
