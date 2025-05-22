package com.example.homeaid.managerboard.controller;

import com.example.homeaid.global.common.response.CommonApiResponse;
import com.example.homeaid.managerboard.dto.request.CreateManagerBoardRequestDto;
import com.example.homeaid.managerboard.dto.request.UpdateManagerBoardRequestDto;
import com.example.homeaid.managerboard.dto.response.CreateManagerBoardResponseDto;
import com.example.homeaid.managerboard.dto.response.UpdateManagerBoardResponseDto;
import com.example.homeaid.managerboard.entity.ManagerBoard;
import com.example.homeaid.managerboard.service.ManagerBoardServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/manager/board")
@RequiredArgsConstructor
public class ManagerBoardController {

  private final ManagerBoardServiceImpl managerBoardService;

  @PostMapping("")
  @Operation(summary = "매니저 게시글 작성", responses = {
      @ApiResponse(responseCode = "201", description = "작성 성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<CreateManagerBoardResponseDto>> createManagerBoard(
      @Valid @RequestBody CreateManagerBoardRequestDto requestDto
  ) {
    ManagerBoard saved = managerBoardService.createManagerBoard(requestDto.toEntity());
    return ResponseEntity.status(HttpStatus.CREATED).body(CommonApiResponse.success(CreateManagerBoardResponseDto.toDto(saved))
    );
  }

  @GetMapping("/{id}")
  @Operation(summary = "매니저 게시글 조회", responses = {
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<CreateManagerBoardResponseDto>> getManagerBoard(
      @PathVariable Long id
  ) {
    ManagerBoard board = managerBoardService.getManagerBoard(id);
    return ResponseEntity.ok(CommonApiResponse.success(CreateManagerBoardResponseDto.toDto(board)));
  }

  @PutMapping("/{id}")
  @Operation(summary = "매니저 게시글 수정", responses = {
      @ApiResponse(responseCode = "200", description = "수정 성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<UpdateManagerBoardResponseDto>> updateManagerBoard(
      @PathVariable Long id,
      @Valid @RequestBody UpdateManagerBoardRequestDto updateManagerRequestDto
  ) {
    return ResponseEntity.status(HttpStatus.OK).body(
        CommonApiResponse.success(UpdateManagerBoardResponseDto.toDto(
            managerBoardService.updateManagerBoard(id, updateManagerRequestDto)
        ))
    );
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
