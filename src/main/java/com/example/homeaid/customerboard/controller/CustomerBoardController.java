package com.example.homeaid.customerboard.controller;

import com.example.homeaid.customerboard.dto.request.CreateBoardRequestDto;
import com.example.homeaid.customerboard.dto.request.UpdateBoardRequestDto;
import com.example.homeaid.customerboard.dto.response.CreateBoardResponseDto;
import com.example.homeaid.customerboard.dto.response.UpdateBoardResponseDto;
import com.example.homeaid.customerboard.entity.CustomerBoard;
import com.example.homeaid.customerboard.service.CustomerBoardServiceImpl;
import com.example.homeaid.global.common.response.CommonApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer/board")
public class CustomerBoardController {

  private final CustomerBoardServiceImpl boardService;

  @PostMapping("")
  @Operation(summary = "[수요자] 게시판 글 작성", responses = {
      @ApiResponse(responseCode = "201", description = "성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<CreateBoardResponseDto>> createBoard(
      @RequestBody @Valid CreateBoardRequestDto createRequestDto
  ) {
    return ResponseEntity.status(HttpStatus.CREATED).body(CommonApiResponse.success(
        CreateBoardResponseDto.toDto(
            boardService.createBoard(CreateBoardRequestDto.toEntity(createRequestDto))))
    );
  }

  @PutMapping("/{id}")
  @Operation(summary = "[수요자] 게시판 글 수정", responses = {
      @ApiResponse(responseCode = "200", description = "성공",
          content = @Content(schema = @Schema(implementation = UpdateBoardResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "해당 게시글 없음",
          content = @Content(schema = @Schema(implementation = UpdateBoardResponseDto.class)))
  })
  public ResponseEntity<CommonApiResponse<UpdateBoardResponseDto>> updateBoard(
      @PathVariable Long id,
      @RequestBody @Valid UpdateBoardRequestDto updateBoardRequestDto
  ) {
    CustomerBoard updatedBoard = boardService.updateBoard(
        id, UpdateBoardRequestDto.toEntity(updateBoardRequestDto)
    );
    return ResponseEntity.status(HttpStatus.OK).body(
        CommonApiResponse.success(UpdateBoardResponseDto.toDto(updatedBoard))
    );
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "[수요자] 게시판 글 삭제", responses = {
      @ApiResponse(responseCode = "200", description = "성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "404", description = "해당 게시글 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<Void>> deleteBoard(
      @PathVariable Long id
  ) {
    boardService.deleteBoard(id);
    return ResponseEntity.ok(CommonApiResponse.success());
  }

  @GetMapping("/{id}")
  @Operation(summary = "[수요자] 게시글 상세조회", responses = {
      @ApiResponse(responseCode = "200", description = "성공",
          content = @Content(schema = @Schema(implementation = CreateBoardResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "해당 게시글 없음",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<CreateBoardResponseDto>> getBoard(
      @PathVariable Long id
  ) {
    return ResponseEntity.ok(
        CommonApiResponse.success(CreateBoardResponseDto.toDto(boardService.getBoard(id))));
  }

  @GetMapping("")
  @Operation(summary = "[수요자] 게시글 전체 조회 및 검색", responses = {
      @ApiResponse(responseCode = "200", description = "성공",
          content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<Page<CreateBoardResponseDto>>> searchBoard(
      @RequestParam(required = false) String keyword,  // 검색 키워드 (제목 또는 내용)
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDirection
  ) {
    Sort sort = sortDirection.equalsIgnoreCase("desc") ?
        Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

    Pageable pageable = PageRequest.of(page, size, sort);

    return ResponseEntity.ok(CommonApiResponse.success(boardService.searchBoard(
        keyword, pageable)));
  }


}
