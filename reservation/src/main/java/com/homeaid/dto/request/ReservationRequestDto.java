package com.homeaid.dto.request;


import com.homeaid.domain.Reservation;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ReservationRequestDto {

  @NotNull
  private Long customerId;

  @NotNull
  private LocalDate requestedDate;

  @NotNull
  private LocalTime requestedTime;

  @NotNull
  private Long subOptionId;

  public static Reservation toEntity(ReservationRequestDto reservationRequestDto) {

    return Reservation.builder()
        .customerId(reservationRequestDto.getCustomerId())
        .requestedDate(reservationRequestDto.getRequestedDate())
        .requestedTime(reservationRequestDto.getRequestedTime())
        .build();

  }

}
