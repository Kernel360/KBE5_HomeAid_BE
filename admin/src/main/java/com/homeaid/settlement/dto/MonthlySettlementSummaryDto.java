package com.homeaid.settlement.dto;

import com.homeaid.settlement.dto.response.SettlementResponseDto;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlySettlementSummaryDto {

  private List<SettlementResponseDto> settlements; // 전체 정산 목록
  private Map<String, WeeklySettlementGroupDto> weeklyGrouped; // 주차별 그룹 + 합계
}
