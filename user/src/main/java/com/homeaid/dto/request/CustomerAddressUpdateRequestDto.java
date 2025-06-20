package com.homeaid.dto.request;

import com.homeaid.domain.CustomerAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "주소 수정 요청 DTO")
public class CustomerAddressUpdateRequestDto {

  @Schema(description = "상세 주소", example = "101동 1001호")
  private String addressDetail;

  public static CustomerAddress toEntity(CustomerAddressUpdateRequestDto requestDto) {
    return CustomerAddress.builder()
        .addressDetail(requestDto.getAddressDetail())
        .build();
  }
}