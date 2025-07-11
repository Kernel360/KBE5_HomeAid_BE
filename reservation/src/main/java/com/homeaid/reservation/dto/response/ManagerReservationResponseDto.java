package com.homeaid.reservation.dto.response;

import com.homeaid.domain.Customer;
import com.homeaid.matching.domain.Matching;
import com.homeaid.reservation.domain.Reservation;
import com.homeaid.matching.controller.enumerate.MatchingStatus;
import com.homeaid.reservation.domain.enumerate.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "매니저용 예약 응답 DTO")
public class ManagerReservationResponseDto {

  @Schema(description = "예약 ID", example = "101")
  private Long reservationId;

  @Schema(description = "매칭 ID", example = "55")
  private Long matchingId;

  @Schema(description = "예약 상태", example = "REQUESTED")
  private ReservationStatus status;

  @Schema(description = "총 가격 (단위: 원)", example = "45000")
  private Integer totalPrice;

  @Schema(description = "총 소요 시간 (단위: 시간)", example = "3")
  private Integer totalDuration;

  @Schema(description = "예약 시작 일시", example = "2025-06-15T14:00:00")
  private LocalDateTime startTime;

  @Schema(description = "예약 고객 이름", example = "홍길동")
  private String customerName;

  @Schema(description = "서비스 옵션 이름", example = "청소")
  private String serviceOptionName;

  @Schema(description = "매칭 상태", example = "REQUESTED")
  private MatchingStatus matchingStatus;


  public static ManagerReservationResponseDto toDto(Reservation reservation, Customer customer, Matching matching) {
    return ManagerReservationResponseDto.builder()
        .reservationId(reservation.getId())
        .matchingId(matching.getId())
        .status(reservation.getStatus())
        .serviceOptionName(reservation.getItem().getServiceOptionName())
        .startTime(LocalDateTime.of(
            reservation.getRequestedDate(),
            reservation.getRequestedTime()
        ))
        .matchingStatus(matching.getStatus())
        .customerName(customer.getName())
        .build();
  }

}
