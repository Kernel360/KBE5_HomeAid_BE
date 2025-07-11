package com.homeaid.matching.dto.response;

import com.homeaid.matching.domain.Matching;
import com.homeaid.matching.controller.enumerate.MatchingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "매니저용 매칭 상세 응답 DTO")
public class ManagerMatchingResponseDto {

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

  @Schema(description = "고객 요청 사항", example = "주방 기름때 제거에 신경써주세요. 욕실 곰팡이도 깔끔하게 청소 부탁드립니다.")
  private String customerRequest;

  @Schema(description = "매칭 상태", example = "REQUESTING")
  private MatchingStatus status;

  @Schema(description = "예약 ID", example = "121")
  private Long reservationId;

  @Schema(description = "주소", example = "서울특별시 강남구 강남대로 364 11층")
  private String fullAddress;

  public static ManagerMatchingResponseDto toDto(Matching matching) {
    return ManagerMatchingResponseDto.builder()
        .matchingId(matching.getId())
        .serviceType(matching.getReservation().getItem().getServiceOptionName())
        .reservedDate(matching.getReservation().getRequestedDate())
        .reservedTime(matching.getReservation().getRequestedTime())
        .estimatedDuration(matching.getReservation().getDuration())
        .fullAddress(matching.getReservation().getAddress() + matching.getReservation().getAddressDetail())
        .customerRequest(matching.getReservation().getCustomerMemo())
        .reservationId(matching.getReservation().getId())
        .status(matching.getStatus())
        .build();
  }

}
