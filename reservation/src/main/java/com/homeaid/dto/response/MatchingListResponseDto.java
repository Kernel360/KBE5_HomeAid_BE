package com.homeaid.dto.response;

import com.homeaid.domain.Matching;
import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.MatchingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@Schema(description = "매칭 응답 DTO")
public class MatchingListResponseDto {

    @Schema(description = "매칭 ID", example = "8")
    private Long matchingId;

    @Schema(description = "id", example = "1L")
    private Long customerId;

    @Schema(description = "서비스 유형", example = "빨래")
    private String subOptionName;

    @Schema(description = "일시", example = "날짜 미정 시간 미정")
    private String scheduledDateTime;

    @Schema(description = "매칭 상태 한글명", example = "매칭 대기")
    private String statusDisplay;

    @Schema(description = "매칭 상태 원본", example = "REQUESTED")
    private String status;

    private Long reservationId;

    public static MatchingListResponseDto toDto(Matching matching) {
        return MatchingListResponseDto.builder()
                .matchingId(matching.getId())
                .customerId(matching.getReservation().getCustomerId())
                .subOptionName(matching.getReservation().getItem().getSubOptionName())
                .scheduledDateTime(formatScheduledDateTime(matching.getReservation()))
                .statusDisplay(getStatusDisplay(matching.getStatus()))
                .status(matching.getStatus().name())
                .reservationId(matching.getReservation().getId())
                .build();
    }

    private static String formatScheduledDateTime(Reservation reservation) {
        if (reservation.getRequestedDate() == null || reservation.getRequestedTime() == null) {
            return "날짜 미정 시간 미정";
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M월 d일");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String date = reservation.getRequestedDate().format(dateFormatter);
        String time = reservation.getRequestedTime().format(timeFormatter);

        return date + " " + time;
    }

    private static String getStatusDisplay(MatchingStatus status) {
        return switch (status) {
            case REQUESTED -> "매칭 대기";
            case ACCEPTED -> "매니저 수락";
            case REJECTED -> "거절됨";
            case CONFIRMED -> "매칭 확정";
            default -> status.name();
        };
    }
}