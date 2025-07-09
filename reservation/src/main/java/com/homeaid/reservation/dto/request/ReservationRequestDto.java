package com.homeaid.reservation.dto.request;


import com.homeaid.reservation.domain.Reservation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
  @Schema(description = "예약 요청 날짜 (yyyy-MM-dd)", example = "2025-06-10")
  private LocalDate requestedDate;

  @NotNull
  @Schema(description = "예약 요청 시간 (HH:mm:ss)", example = "14:00:00")
  private LocalTime requestedTime;

  @NotNull
  @Schema(description = "선택한 서비스 옵션 ID", example = "3")
  private Long optionId;

  @NotNull
  @Schema(description = "선택한 서비스 소요 시간(시간)", example = "3")
  private Integer totalDuration;

  @NotBlank
  @Schema(description = "주소 정보", example = "서울시 강남구 강남대로 15")
  private String address;

  @NotBlank
  @Schema(description = "주소 상세 정보", example = "2층")
  private String addressDetail;

  @NotNull
  @Schema(description = "위도", example = "37.495708585432276")
  private Double latitude;

  @NotNull
  @Schema(description = "경도", example = "127.02894600148066")
  private Double longitude;

  public static Reservation toEntity(ReservationRequestDto reservationRequestDto) {
    return Reservation.builder()
        .requestedDate(reservationRequestDto.getRequestedDate())
        .requestedTime(reservationRequestDto.getRequestedTime())
        .latitude(reservationRequestDto.getLatitude())
        .longitude(reservationRequestDto.getLongitude())
        .duration(reservationRequestDto.getTotalDuration())
        .address(reservationRequestDto.getAddress())
        .addressDetail(reservationRequestDto.getAddressDetail())
        .build();
  }

}
