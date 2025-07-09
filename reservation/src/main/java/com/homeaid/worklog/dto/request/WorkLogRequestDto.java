package com.homeaid.worklog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "매니저의 예약건에 대한 체크인 요청")
public class WorkLogRequestDto {

    @NotNull(message = "위도값은 필수 입니다")
    @Schema(description = "위도 값", example = "127.029733323803")
    private Double lat;

    @NotNull(message = "경도값은 필수 입니다")
    @Schema(description = "위도 값", example = "37.4946490234479")
    private Double lng;

}
