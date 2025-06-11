package com.homeaid.dto.request;

import com.homeaid.domain.CustomerAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "주소 수정 요청 DTO")
public class CustomerAddressUpdateRequestDto {

  @NotBlank(message = "주소는 필수입니다")
  @Schema(description = "기본 주소", example = "서울특별시 강남구 역삼동 123-45")
  private String address;

  @Schema(description = "상세 주소", example = "101동 1001호")
  private String addressDetail;

  @Schema(description = "위도", example = "37.5665")
  private Double latitude;

  @Schema(description = "경도", example = "126.9780")
  private Double longitude;

  public static CustomerAddress toEntity(@Valid CustomerAddressUpdateRequestDto requestDto) {
    return CustomerAddress.builder()
        .address(requestDto.getAddress())
        .addressDetail(requestDto.getAddressDetail())
        .latitude(requestDto.getLatitude())
        .longitude(requestDto.getLongitude())
        .build();
  }
}