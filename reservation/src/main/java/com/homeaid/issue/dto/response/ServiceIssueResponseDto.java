package com.homeaid.issue.dto.response;

import com.homeaid.issue.domain.ServiceIssue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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

  @Schema(description = "이슈 이미지 목록")
  private List<ImageDto> images;

  @Schema(description = "이슈 작성일")
  private LocalDateTime createdAt;

  @Getter
  @Builder
  @Schema(description = "서비스 이슈 이미지")
  public static class ImageDto {

    @Schema(description = "이미지 ID")
    private Long id;

    @Schema(description = "이미지 원본 파일명")
    private String originalName;

    @Schema(description = "이미지 URL")
    private String url;
  }

  public static ServiceIssueResponseDto toDto(ServiceIssue serviceIssue) {
    return ServiceIssueResponseDto.builder()
        .id(serviceIssue.getId())
        .reservationId(serviceIssue.getReservation().getId())
        .content(serviceIssue.getContent())
        .createdAt(serviceIssue.getCreatedAt())
        .images(serviceIssue.getImages().stream()
            .map(image -> ImageDto.builder()
                .id(image.getId())
                .originalName(image.getOriginalName())
                .url(image.getUrl())
                .build())
            .collect(Collectors.toList()))
        .build();
  }
}
