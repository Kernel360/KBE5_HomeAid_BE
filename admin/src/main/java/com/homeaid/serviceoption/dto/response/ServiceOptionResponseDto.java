package com.homeaid.serviceoption.dto.response;

import com.homeaid.serviceoption.domain.ServiceOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "서비스 상위 옵션 응답 DTO")
public class ServiceOptionResponseDto {

  @Schema(description = "옵션 ID", example = "1")
  private Long id;

  @Schema(description = "옵션 이름", example = "청소")
  private String name;

  @Schema(description = "옵션 설명", example = "기본 청소 서비스입니다.")
  private String description;

  @Schema(description = "등록일시", example = "2025-06-01T15:00:00")
  private LocalDateTime createdAt;

  public static ServiceOptionResponseDto toDto(ServiceOption option) {
    return ServiceOptionResponseDto.builder()
        .id(option.getId())
        .name(option.getName())
        .description(option.getDescription())
        .createdAt(option.getCreatedAt())
        .build();
  }
}