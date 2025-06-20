package com.homeaid.dto.response;

import com.homeaid.domain.CustomerAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "주소 응답 DTO")
public class CustomerAddressResponseDto {

  @Schema(description = "주소 ID", example = "1")
  private Long id;

  @Schema(description = "기본 주소", example = "서울특별시 강남구 역삼동 123-45")
  private String address;

  @Schema(description = "상세 주소", example = "101동 1001호")
  private String addressDetail;

  @Schema(description = "위도", example = "37.5665")
  private Double latitude;

  @Schema(description = "경도", example = "126.9780")
  private Double longitude;

  @Schema(description = "전체 주소 (기본주소 + 상세주소)", example = "서울특별시 강남구 역삼동 123-45 101동 1001호")
  private String fullAddress;

  @Schema(description = "별칭", example = "집")
  private String alias;

  public static CustomerAddressResponseDto toDto(CustomerAddress address) {
    String fullAddress = address.getAddress();
    if (address.getAddressDetail() != null && !address.getAddressDetail().trim().isEmpty()) {
      fullAddress += " " + address.getAddressDetail();
    }

    return CustomerAddressResponseDto.builder()
        .id(address.getId())
        .address(address.getAddress())
        .addressDetail(address.getAddressDetail())
        .latitude(address.getLatitude())
        .longitude(address.getLongitude())
        .fullAddress(fullAddress)
        .alias(address.getAlias())
        .build();
  }
}