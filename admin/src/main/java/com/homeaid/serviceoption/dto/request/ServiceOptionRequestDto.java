package com.homeaid.serviceoption.dto.request;

import com.homeaid.serviceoption.domain.ServiceOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "서비스 상위 옵션 등록/수정 요청 DTO")
public class ServiceOptionRequestDto {

    @NotBlank
    @Schema(description = "옵션 이름", example = "청소")
    private String name;

    @Schema(description = "옵션에서 제공하는 서비스", example = "[\"배란다 청소\", \"욕실 청소\", \"창문 청소\"]")
    private List<String> features;

    @Schema(description = "서비스 가격", example = "30000")
    private Integer price;

    public static ServiceOption toEntity(ServiceOptionRequestDto serviceSubOptionRequestDto) {
        return ServiceOption.builder().name(serviceSubOptionRequestDto.getName())
                .price(serviceSubOptionRequestDto.getPrice())
                .features(serviceSubOptionRequestDto.getFeatures()).build();
    }
}