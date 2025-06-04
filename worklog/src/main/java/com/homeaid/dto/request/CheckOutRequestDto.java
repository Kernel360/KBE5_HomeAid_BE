package com.homeaid.dto.request;

import com.homeaid.domain.WorkLog;
import com.homeaid.domain.enumerate.WorkType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "매니저의 예약건에 대한 체크인 요청")
public class CheckOutRequestDto {

    @NotNull
    @Schema(description = "작업기록 id", example = "10")
    private Long workLogId;

    @NotNull(message = "위도값은 필수 입니다")
    @Schema(description = "위도 값", example = "127.029733323803")
    private Double lat;

    @NotNull(message = "경도값은 필수 입니다")
    @Schema(description = "위도 값", example = "37.4946490234479")
    private Double lng;

    @NotNull
    @Schema(example = "1")
    private Long managerId;

    @NotNull
    @Schema(example = "1")
    private Long reservationId;

    public static WorkLog toEntity(CheckOutRequestDto checkOutRequestDto) {
        return WorkLog.builder()
                .workType(WorkType.CHECKOUT)
                .managerId(checkOutRequestDto.managerId)
                .build();
    }

}
