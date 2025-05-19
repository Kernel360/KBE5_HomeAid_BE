package com.example.homeaid.customerboard.controller;

import com.example.homeaid.customerboard.dto.request.CreateBoardRequestDto;
import com.example.homeaid.customerboard.dto.response.CreateBoardResponseDto;
import com.example.homeaid.customerboard.entity.CustomerBoard;
import com.example.homeaid.customerboard.service.CustomerBoardServiceImpl;

import com.example.homeaid.global.common.response.CommonApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer/board")
public class CustomerBoardController {

    private final CustomerBoardServiceImpl boardService;

    @PostMapping
    @Operation(summary = "게시글 작성", responses = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
    })
    public ResponseEntity<CommonApiResponse<CreateBoardResponseDto>> createBoard(
        CreateBoardRequestDto createRequestDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonApiResponse.success(
            CreateBoardResponseDto.toDto(
                boardService.createBoard(CreateBoardRequestDto.toEntity(createRequestDto)))));
    }

    @PutMapping("/{id}")
    public CreateBoardResponseDto updateBoard(
        @PathVariable Long id, @RequestBody CreateBoardRequestDto dto
    ) {
        return CreateBoardResponseDto.toDto(boardService.updateBoard(id));
    }


    @GetMapping("/{id}")
    @Operation(summary = "게시글 조회", responses = {
        @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(schema = @Schema(implementation = CreateBoardResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "게시판 없음",
            content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
    })
    public ResponseEntity<CommonApiResponse<CreateBoardResponseDto>> getBoard(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(
            CommonApiResponse.success(CreateBoardResponseDto.toDto(boardService.getBoard(id))));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<CommonApiResponse<Void>> deleteBoard(
        @PathVariable Long id
    ) {
        boardService.deleteBoard(id);
        return ResponseEntity.ok(CommonApiResponse.success());
    }


}
