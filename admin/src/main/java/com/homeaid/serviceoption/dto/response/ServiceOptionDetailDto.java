package com.homeaid.serviceoption.dto.response;

import com.homeaid.serviceoption.domain.ServiceOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@Schema(description = "상위 옵션 + 하위 옵션 목록 포함 응답 DTO")
public class ServiceOptionDetailDto {

  @Schema(description = "옵션 ID", example = "1")
  private Long id;

  @Schema(description = "옵션 이름", example = "청소")
  private String name;





  @Schema(description = "등록일시", example = "2025-06-01T15:00:00")
  private LocalDateTime createdAt;

  public static List<ServiceOptionDetailDto> toDto(List<ServiceOption> serviceOptionList) {
    return serviceOptionList.stream()
        .map(ServiceOptionDetailDto::toDto)
        .collect(Collectors.toList());
  }

  public static ServiceOptionDetailDto toDto(ServiceOption option) {
    return ServiceOptionDetailDto.builder()
        .id(option.getId())
        .name(option.getName())
        .createdAt(option.getCreatedAt())
        .build();
  }
}
