package com.example.homeaid.managerboard.controller;

import com.example.homeaid.global.common.response.CommonApiResponse;
import com.example.homeaid.managerboard.dto.request.ManagerBoardCreateRequestDto;
import com.example.homeaid.managerboard.dto.request.ManagerBoardUpdateRequestDto;
import com.example.homeaid.managerboard.dto.response.ManagerBoardResponseDto;
import com.example.homeaid.managerboard.service.ManagerBoardServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.pulsar.PulsarAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/manager/board")
@RequiredArgsConstructor
public class ManagerBoardController {

  private final ManagerBoardServiceImpl managerBoardService;

  @PostMapping
  @Operation(summary = "매니저 게시글 작성", responses = {
      @ApiResponse(responseCode = "200", description = "성공",
      content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<ManagerBoardResponseDto>> createManagerBoard(
      ManagerBoardCreateRequestDto managerBoardCreateRequestDto) {

    return ResponseEntity.status(HttpStatus.CREATED).body(CommonApiResponse.success(
        ManagerBoardResponseDto.toDto(
            managerBoardService.createManagerBoard(ManagerBoardCreateRequestDto.toEntity(managerBoardCreateRequestDto))
        )
    ));
  }

  @GetMapping("/{id}")
  @Operation(summary = "매니저 게시글 조회", responses = {
      @ApiResponse(responseCode = "200", description = "성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<ManagerBoardResponseDto>> getManagerBoard(
      @PathVariable Long id
  ) {
    return ResponseEntity.ok(
        CommonApiResponse.success(ManagerBoardResponseDto.toDto(managerBoardService.getManagerBoard(id)))
    );
  }

  @PutMapping("/{id}")
  @Operation(summary = "매니저 게시글 수정", responses = {
      @ApiResponse(responseCode = "200", description = "수정 성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<ManagerBoardResponseDto>> updateManagerBoard(
      @PathVariable Long id,
      @RequestBody ManagerBoardUpdateRequestDto managerBoardUpdateRequestDto
  ) {
    return ResponseEntity.ok(CommonApiResponse.success(
        ManagerBoardResponseDto.toDto(
            managerBoardService.updateManagerBoard(id, managerBoardUpdateRequestDto)
        )
    ));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "매니저 게시글 삭제", responses = {
      @ApiResponse(responseCode = "200", description = "삭제 성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<Void>> deleteManagerBoard(@PathVariable Long id) {
    managerBoardService.deleteManagerBoard(id);
    return ResponseEntity.ok(CommonApiResponse.success());
  }

}
