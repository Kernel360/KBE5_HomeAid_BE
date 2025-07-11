package com.homeaid.matching.dto.response;

import com.homeaid.matching.domain.Matching;
import com.homeaid.matching.controller.enumerate.MatchingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "매칭 상세 응답 DTO")
public class MatchingResponseDto {

  @Schema(description = "매칭 ID", example = "1001")
  private Long matchingId;

  @Schema(description = "서비스 유형", example = "대청소")
  private String serviceType;

  @Schema(description = "예약 날짜", example = "2023-06-15")
  private LocalDate reservedDate;

  @Schema(description = "예약 시간", example = "14:00")
  private LocalTime reservedTime;

  @Schema(description = "예상 소요 시간 (단위: 시간)", example = "3")
  private int estimatedDuration;

  @Schema(description = "위도", example = "37.498095")
  private Double latitude;

  @Schema(description = "경도", example = "127.027610")
  private Double longitude;

  @Schema(description = "고객 요청 사항", example = "주방 기름때 제거에 신경써주세요. 욕실 곰팡이도 깔끔하게 청소 부탁드립니다.")
  private String customerRequest;

  @Schema(description = "매니저 매칭 상태", example = "REQUESTED, ACCEPTED, REJECTED")
  private MatchingStatus managerStatus;

  @Schema(description = "고객 매칭 상태", example = "WAITING, CONFIRMED, REJECTED")
  private MatchingStatus customerStatus;

  @Schema(description = "매칭 상태", example = "REQUESTING")
  private MatchingStatus status;

  @Schema(description = "예약 ID", example = "121")
  private Long reservationId;

  @Schema(description = "주소", example = "서울특별시 강남구 강남대로 364 11층")
  private String fullAddress;

  public static MatchingResponseDto toDto(Matching matching) {
    return MatchingResponseDto.builder()
        .matchingId(matching.getId())
        .serviceType(matching.getReservation().getItem().getServiceOptionName())
        .reservedDate(matching.getReservation().getRequestedDate())
        .reservedTime(matching.getReservation().getRequestedTime())
        .estimatedDuration(matching.getReservation().getDuration())
        .latitude(matching.getReservation().getLatitude())
        .longitude(matching.getReservation().getLongitude())
        .customerRequest(matching.getReservation().getCustomerMemo())
        .managerStatus(matching.getStatus())
        .customerStatus(matching.getStatus())
            .status(matching.getStatus())
            .reservationId(matching.getReservation().getId())
        .build();
  }

}