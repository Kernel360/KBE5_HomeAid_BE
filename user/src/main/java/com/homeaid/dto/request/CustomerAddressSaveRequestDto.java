package com.homeaid.dto.request;

import com.homeaid.domain.CustomerAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "주소 저장 요청 DTO")
public class CustomerAddressSaveRequestDto {

  @NotBlank(message = "주소는 필수입니다")
  @Schema(description = "기본 주소", example = "서울특별시 강남구 역삼동 123-45")
  private String address;

  @NotNull
  @Schema(description = "상세 주소", example = "101동 1001호")
  private String addressDetail;

  @NotNull
  @Schema(description = "위도", example = "37.5665")
  private Double latitude;

  @NotNull
  @Schema(description = "경도", example = "126.9780")
  private Double longitude;

  @Schema(description = "별칭", example = "집")
  private String alias;

  public static CustomerAddress toEntity(CustomerAddressSaveRequestDto requestDto) {
    CustomerAddress customerAddress = CustomerAddress.builder()
        .address(requestDto.getAddress())
        .addressDetail(requestDto.getAddressDetail())
        .latitude(requestDto.getLatitude())
        .longitude(requestDto.getLongitude())
        .build();

    if (requestDto.getAlias() != null) {
      customerAddress.setAlias(requestDto.getAlias());
    } else {
      customerAddress.setAlias(requestDto.getAddress());
    }
    return customerAddress;
  }

}
