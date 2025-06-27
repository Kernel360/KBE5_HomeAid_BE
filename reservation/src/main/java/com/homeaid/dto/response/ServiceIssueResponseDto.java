package com.homeaid.dto.response;

import com.homeaid.domain.ServiceIssue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "이슈 응답 DTO")
public class ServiceIssueResponseDto {

  @Schema(description = "서비스 이슈 ID")
  private Long id;

  @Schema(description = "예약 ID")
  private Long reservationId;

  @Schema(description = "이슈 내용")
  private String content;

  @Schema(description = "이슈 작성일")
  private LocalDateTime createdAt;

  public static ServiceIssueResponseDto toDto(ServiceIssue serviceIssue) {
    return ServiceIssueResponseDto.builder()
        .id(serviceIssue.getId())
        .reservationId(serviceIssue.getReservation().getId())
        .content(serviceIssue.getContent())
        .createdAt(serviceIssue.getCreatedAt())
        .build();
  }

}
