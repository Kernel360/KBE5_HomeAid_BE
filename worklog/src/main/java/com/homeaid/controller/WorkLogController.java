package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.WorkLog;
import com.homeaid.dto.request.CheckInRequestDto;
import com.homeaid.dto.request.CheckOutRequestDto;
import com.homeaid.dto.response.CheckInResponseDto;
import com.homeaid.service.WorkLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/manager/work-logs")
@RestController
public class WorkLogController {
    private final WorkLogService workLogService;

    @PostMapping
    @Operation(summary = "체크인 요청", description = "매니저의 예약건에 대한 체크인 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "체크인 성공"),
            @ApiResponse(responseCode = "400", description = "이미 체크인 한 예약건"),
            @ApiResponse(responseCode = "403", description = "예약 위치 범위 밖의 잘못된 요청")
    })
    public ResponseEntity<CommonApiResponse<CheckInResponseDto>> createCheckIn(@RequestBody @Valid CheckInRequestDto checkInRequestDto) {
        WorkLog workLog = workLogService.createWorkLog(CheckInRequestDto.toEntity(checkInRequestDto),
                checkInRequestDto.getReservationId(), checkInRequestDto.getLat(), checkInRequestDto.getLng());

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonApiResponse.success(CheckInResponseDto.toDto(workLog)));
    }

    @PatchMapping
    @Operation(summary = "체크아웃 요청", description = "매니저의 예약건에 대한 체크아웃 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "체크아웃 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 예약 건"),
            @ApiResponse(responseCode = "403", description = "예약 위치 범위 밖의 잘못된 요청")
    })
    public ResponseEntity<CommonApiResponse<Void>> updateWorkLogForCheckOut(@RequestBody @Valid CheckOutRequestDto checkOutRequestDto) {
        workLogService.updateWorkLogForCheckOut(CheckOutRequestDto.toEntity(checkOutRequestDto), checkOutRequestDto.getWorkLogId(), checkOutRequestDto.getLat(), checkOutRequestDto.getLng());

        return ResponseEntity.ok(CommonApiResponse.success());
    }

}
