package com.homeaid.serviceoption.dto.request;

import com.homeaid.serviceoption.domain.ServiceSubOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "서비스 하위 옵션 등록/수정 요청 DTO")
public class ServiceSubOptionRequestDto {

  @NotBlank
  @Schema(description = "하위 옵션 이름", example = "화장실 청소")
  private String name;

  @Schema(description = "옵션 설명", example = "화장실 구역에 대한 집중 청소")
  private String description;

  @NotNull
  @Min(1)
  @Schema(description = "예상 소요 시간(분)", example = "60")
  private Integer durationMinutes;

  @NotNull
  @Min(1000)
  @Schema(description = "기본 가격(원)", example = "15000")
  private Integer basePrice;

  public static ServiceSubOption toEntity(ServiceSubOptionRequestDto serviceSubOptionRequestDto) {
    return ServiceSubOption.builder().name(serviceSubOptionRequestDto.getName())
        .description(serviceSubOptionRequestDto.getDescription())
        .durationMinutes(serviceSubOptionRequestDto.getDurationMinutes())
        .basePrice(serviceSubOptionRequestDto.getBasePrice()).build();
  }
}
