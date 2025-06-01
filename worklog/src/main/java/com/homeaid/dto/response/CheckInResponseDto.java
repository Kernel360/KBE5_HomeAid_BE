package com.homeaid.dto.response;

import com.homeaid.domain.WorkLog;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CheckInResponseDto {
    private LocalDateTime checkInDate;
    private Long worklogId;
    private Long reservationId;

    public static CheckInResponseDto toDto(WorkLog workLog) {
        return CheckInResponseDto.builder()
                .checkInDate(workLog.getCheckInTime())
                .worklogId(workLog.getId())
                .reservationId(workLog.getReservation().getId())
                .build();
    }
}
