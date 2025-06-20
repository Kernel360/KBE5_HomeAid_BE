package com.homeaid.user.dto.request;

import com.homeaid.domain.enumerate.ManagerStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminManagerSearchRequestDto {

  @Schema(description = "매니저 이름 (부분 일치 검색)")
  private String name;

  @Schema(description = "매니저 전화번호 (부분 일치 검색)")
  private String phone;

  @Schema(description = "경력 내용 (부분 일치 검색)")
  private String career;

  @Schema(description = "매니저 상태", example = "ACTIVE")
  private ManagerStatus status;

}
