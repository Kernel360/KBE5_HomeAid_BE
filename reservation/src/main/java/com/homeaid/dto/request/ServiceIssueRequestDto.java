package com.homeaid.dto.request;

import com.homeaid.domain.ServiceIssue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "서비스 이슈 등록 DTO")
public class ServiceIssueRequestDto {

  @Schema(description = "이슈 내용", example = "서비스 제공에 문제가 생겼습니다.")
  private String content;

  public static ServiceIssue toEntity(ServiceIssueRequestDto serviceIssueRequestDto) {
    return ServiceIssue.builder()
        .content(serviceIssueRequestDto.getContent())
        .build();

  }

}
