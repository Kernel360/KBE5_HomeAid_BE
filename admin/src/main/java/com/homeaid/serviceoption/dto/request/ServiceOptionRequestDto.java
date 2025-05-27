package com.homeaid.serviceoption.dto.request;

import com.homeaid.serviceoption.domain.ServiceOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "서비스 상위 옵션 등록/수정 요청 DTO")
public class ServiceOptionRequestDto {

  @NotBlank
  @Schema(description = "옵션 이름", example = "청소")
  private String name;

  @Schema(description = "옵션 설명", example = "기본 청소 서비스입니다.")
  private String description;

  public static ServiceOption toEntity(ServiceOptionRequestDto serviceSubOptionRequestDto) {
    return ServiceOption.builder().name(serviceSubOptionRequestDto.getName())
        .description(serviceSubOptionRequestDto.getDescription()).build();
  }
}