package com.homeaid.reservation.dto.response;


import com.homeaid.reservation.domain.Reservation;
import com.homeaid.matching.controller.enumerate.MatchingStatus;
import com.homeaid.reservation.domain.enumerate.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
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

  @Schema(description = "총 소요 시간 (단위: 시간)", example = "3")
  private Integer totalDuration;

  @Schema(description = "고객 ID", example = "1")
  private Long customerId;

  @Schema(description = "매니저 ID", example = "2")
  private Long managerId;

  @Schema(description = "서비스 이용 날짜")
  private LocalDate requestedDate;

  @Schema(description = "서비스 이용 시간")
  private LocalTime requestedTime;

  @Schema(description = "주소 정보")
  private String address;

  @Schema(description = "상세 주소 정보")
  private String addressDetail;

  @Schema(description = "예약 시작 일시", example = "2025-06-15T14:00:00")
  private LocalDateTime startTime;

  @Schema(description = "예약 고객 이름", example = "홍길동")
  private String customerName;

  @Schema(description = "매칭된 매니저 이름", example = "김매니저")
  private String matchedManagerName;

  @Schema(description = "서비스 옵션 이름", example = "청소")
  private String serviceOptionName;

  @Schema(description = "매칭 상태", example = "REQUESTED")
  private MatchingStatus matchingStatus;

  @Schema(description = "고객 메모", example = "집에 개가 있어요.")
  private String customerMemo;

  @Schema(description = "매칭 ID", example = "99")
  private Long matchingId;

  public static ReservationResponseDto toDto(Reservation reservation, String customerName, String managerName) {
    return ReservationResponseDto.builder()
        .reservationId(reservation.getId())
        .status(reservation.getStatus())
        .totalPrice(reservation.getTotalPrice())
        .totalDuration(reservation.getDuration())
        .serviceOptionName(reservation.getItem().getServiceOptionName())
        .startTime(LocalDateTime.of(
            reservation.getRequestedDate(),
            reservation.getRequestedTime()
        ))
        .customerName(customerName)
        .matchedManagerName(managerName)
        .build();
  }

  public static ReservationResponseDto toDto(Reservation reservation, MatchingStatus matchingStatus, String managerName, Long matchingId) {
    return ReservationResponseDto.builder()
        .reservationId(reservation.getId())
        .status(reservation.getStatus())
        .totalPrice(reservation.getTotalPrice())
        .totalDuration(reservation.getDuration())
        .serviceOptionName(reservation.getItem().getServiceOptionName())
        .customerId(reservation.getCustomer().getId())
        .startTime(LocalDateTime.of(
            reservation.getRequestedDate(),
            reservation.getRequestedTime()
        ))
        .matchedManagerName(managerName)
        .address(reservation.getAddress())
        .addressDetail(reservation.getAddressDetail())
        .matchingStatus(matchingStatus)
        .matchingId(matchingId)
        .build();
  }

  public static ReservationResponseDto toDto(Reservation reservation) {
    return ReservationResponseDto.builder()
        .reservationId(reservation.getId())
        .status(reservation.getStatus())
        .totalPrice(reservation.getTotalPrice())
        .totalDuration(reservation.getDuration())
        .serviceOptionName(reservation.getItem().getServiceOptionName())
        .customerId(reservation.getCustomer().getId())
        .managerId(reservation.getManagerId())
        .requestedDate(reservation.getRequestedDate())
        .requestedTime(reservation.getRequestedTime())
            .address(reservation.getAddress())
            .addressDetail(reservation.getAddressDetail())
            .customerMemo(reservation.getCustomerMemo())
        .build();
  }

}
