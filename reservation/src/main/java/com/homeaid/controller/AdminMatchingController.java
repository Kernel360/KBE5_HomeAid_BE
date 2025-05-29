package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.dto.request.CreateMatchingRequestDto;
import com.homeaid.service.AdminMatchingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/matchings")
@RequiredArgsConstructor
public class AdminMatchingController {

  private final AdminMatchingService adminMatchingService;

  @PostMapping
  public ResponseEntity<CommonApiResponse<Long>> createMatching(
      @Valid @RequestBody CreateMatchingRequestDto matchingRequestDto) {

    Long matchingId = adminMatchingService.createMatching(matchingRequestDto.getManagerId(),
        matchingRequestDto.getReservationId(), CreateMatchingRequestDto.toEntity());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(matchingId));
  }


}
