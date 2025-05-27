package com.homeaid.serviceoption.dto.response;

import com.homeaid.serviceoption.domain.ServiceSubOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "서비스 하위 옵션 응답 DTO")
public class ServiceSubOptionResponseDto {

  @Schema(description = "하위 옵션 ID", example = "3")
  private Long id;

  @Schema(description = "하위 옵션 이름", example = "화장실 청소")
  private String name;

  @Schema(description = "하위 옵션 설명", example = "화장실 전용 청소 서비스입니다.")
  private String description;

  @Schema(description = "소요 시간(분)", example = "60")
  private Integer durationMinutes;

  @Schema(description = "기본 가격(원)", example = "15000")
  private Integer basePrice;

  @Schema(description = "등록일시", example = "2025-06-01T15:00:00")
  private LocalDateTime createdAt;

  public static ServiceSubOptionResponseDto toDto(ServiceSubOption subOption) {
    return ServiceSubOptionResponseDto.builder()
        .id(subOption.getId())
        .name(subOption.getName())
        .description(subOption.getDescription())
        .durationMinutes(subOption.getDurationMinutes())
        .basePrice(subOption.getBasePrice())
        .createdAt(subOption.getCreatedAt())
        .build();
  }
}