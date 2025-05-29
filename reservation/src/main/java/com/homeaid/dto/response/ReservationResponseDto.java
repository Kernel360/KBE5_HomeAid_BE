package com.homeaid.dto.response;


import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationResponseDto {

  private Long reservationId;
  private ReservationStatus status;
  private Integer totalPrice;
  private Integer totalDuration;

  public static ReservationResponseDto toDto(Reservation reservation) {
    return ReservationResponseDto.builder()
        .reservationId(reservation.getId())
        .status(reservation.getStatus())
        .totalPrice(reservation.getTotalPrice())
        .totalDuration(reservation.getTotalDuration())
        .build();
  }
}
