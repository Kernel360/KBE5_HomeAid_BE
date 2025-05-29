package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.WorkLog;
import com.homeaid.dto.request.CheckInRequestDto;
import com.homeaid.dto.response.CheckInResponseDto;
import com.homeaid.service.WorkLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/work-log")
@RestController
public class WorkLogController {
    private final WorkLogService workLogService;

    @PostMapping
    @Operation(summary = "체크인 요청", description = "매니저의 예약건에 대한 체크인 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 수정 성공"),
            @ApiResponse(responseCode = "403", description = "예약 위치 범위 밖의 잘못된 요청")
    })
    public ResponseEntity<CommonApiResponse<CheckInResponseDto>> createCheckIn(@RequestBody @Valid CheckInRequestDto checkInRequestDto) {
        Point point = new Point(checkInRequestDto.getLat(), checkInRequestDto.getLng());
        WorkLog workLog = workLogService.createWorkLog(CheckInRequestDto.toEntity(checkInRequestDto),
                checkInRequestDto.getReservationId(), point);

        return ResponseEntity.ok(CommonApiResponse.success(CheckInResponseDto.toDto(workLog)));
    }

}
