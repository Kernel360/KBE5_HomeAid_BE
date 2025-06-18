package com.homeaid.settlement.dto.request;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettlementRequestDto {

  private Long managerId;
  private LocalDate from;  // 시작일 (예: 월요일)
  private LocalDate to;    // 종료일 (예: 수요일, 일요일 등)

}
