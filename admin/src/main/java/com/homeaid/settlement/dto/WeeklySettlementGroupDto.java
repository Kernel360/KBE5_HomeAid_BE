package com.homeaid.settlement.dto;

import com.homeaid.settlement.dto.response.SettlementResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeeklySettlementGroupDto {

  private Integer totalAmount;       // 총 결제금액
  private Integer managerAmount;     // 매니저 몫 (80%)
  private Integer adminAmount;       // 관리자 수수료 (20%)
  private List<SettlementResponseDto> settlements;
}
