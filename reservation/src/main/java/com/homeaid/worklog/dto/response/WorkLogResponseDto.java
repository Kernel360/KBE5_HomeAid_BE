package com.homeaid.worklog.dto.response;

import com.homeaid.worklog.domain.WorkLog;
import com.homeaid.worklog.domain.enumerate.WorkType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "매니저 근무 기록 DTO")
public class WorkLogResponseDto {

    @Schema(description = "체크인 시간", example = "2025-06-06T09:00:00")
    private LocalDateTime checkInTime;

    @Schema(description = "체크아웃 시간", example = "2025-06-06T12:00:00")
    private LocalDateTime checkOutTime;

    @Schema(description = "근무 상태", example = "COMPLETED")
    private WorkType status;

    public static WorkLogResponseDto toDto(WorkLog workLog) {
        return WorkLogResponseDto.builder()
            .checkInTime(workLog.getCheckInTime())
            .checkOutTime(workLog.getCheckOutTime())
            .status(workLog.getWorkType())
            .build();
    }
}