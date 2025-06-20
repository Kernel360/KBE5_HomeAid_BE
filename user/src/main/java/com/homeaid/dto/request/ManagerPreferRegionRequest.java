package com.homeaid.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerPreferRegionRequest {

  @NotBlank(message = "시도는 비어 있을 수 없습니다.")
  private String sido;

  @NotBlank(message = "시군구는 비어 있을 수 없습니다.")
  private String sigungu;

}
