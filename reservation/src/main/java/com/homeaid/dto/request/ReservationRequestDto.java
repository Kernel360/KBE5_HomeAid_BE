package com.homeaid.dto.request;


import com.homeaid.domain.Reservation;
import io.swagger.v3.oas.annotations.media.Schema;
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
  @Schema(description = "예약을 요청하는 고객의 ID", example = "1")
  private Long customerId;

  @NotNull
  @Schema(description = "예약 요청 날짜 (yyyy-MM-dd)", example = "2025-06-10")
  private LocalDate requestedDate;

  @NotNull
  @Schema(description = "예약 요청 시간 (HH:mm:ss)", example = "14:00:00")
  private LocalTime requestedTime;

  @NotNull
  @Schema(description = "선택한 서비스 하위 옵션 ID", example = "3")
  private Long subOptionId;

  public static Reservation toEntity(ReservationRequestDto reservationRequestDto) {

    return Reservation.builder()
        .customerId(reservationRequestDto.getCustomerId())
        .requestedDate(reservationRequestDto.getRequestedDate())
        .requestedTime(reservationRequestDto.getRequestedTime())
        .build();

  }

}
