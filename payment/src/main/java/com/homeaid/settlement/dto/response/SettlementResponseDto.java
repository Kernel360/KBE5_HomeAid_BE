package com.homeaid.settlement.dto.response;

import com.homeaid.settlement.domain.Settlement;
import com.homeaid.settlement.domain.enumerate.SettlementStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettlementResponseDto {

  private Long id;
  private Long managerId;
  private LocalDate from;
  private LocalDate to;
  private Integer totalAmount; // 총 결제금액
  private Integer managerAmount; // 매니저 80
  private Integer adminAmount; // 관리자 20 DB 저장 X
  private LocalDateTime settledAt; // 정산 생성일
  private LocalDateTime confirmedAt; // 관리자 승인일
  private LocalDateTime paidAt;      // 관리자 지급일
  private SettlementStatus status;

  public static SettlementResponseDto from(Settlement s) {
    Integer managerAmount = (int) Math.round(s.getTotalAmount() * 0.8);
    Integer adminAmount = s.getTotalAmount() - managerAmount;

    return SettlementResponseDto.builder()
        .id(s.getId())
        .managerId(s.getManagerId())
        .from(s.getSettlementWeekStart())
        .to(s.getSettlementWeekEnd())
        .totalAmount(s.getTotalAmount())
        .managerAmount(managerAmount)
        .adminAmount(adminAmount)
        .settledAt(s.getSettledAt())
        .confirmedAt(s.getConfirmedAt())
        .paidAt(s.getPaidAt())
        .status(s.getStatus())
        .build();
  }

}
