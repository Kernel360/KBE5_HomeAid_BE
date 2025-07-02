package com.homeaid.settlement.dto;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.settlement.domain.Settlement;
import com.homeaid.settlement.domain.enumerate.SettlementStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettlementWithManagerResponseDto {

  private Long id;
  private LocalDate from;
  private LocalDate to;
  private Integer totalAmount;
  private Integer managerAmount;
  private Integer adminAmount;
  private LocalDateTime settledAt;
  private LocalDateTime confirmedAt;
  private LocalDateTime paidAt;
  private SettlementStatus status;

  private Long managerId;
  private String managerName;
  private String managerPhone;
  private String managerEmail;
  private ManagerStatus managerStatus;

  public static SettlementWithManagerResponseDto from(Settlement settlement, Manager manager) {
    Integer managerAmount = (int) Math.round(settlement.getTotalAmount() * 0.8);
    Integer adminAmount = settlement.getTotalAmount() - managerAmount;

    return SettlementWithManagerResponseDto.builder()
        .id(settlement.getId())
        .from(settlement.getSettlementWeekStart())
        .to(settlement.getSettlementWeekEnd())
        .totalAmount(settlement.getTotalAmount())
        .managerAmount(managerAmount)
        .adminAmount(adminAmount)
        .settledAt(settlement.getSettledAt())
        .confirmedAt(settlement.getConfirmedAt())
        .paidAt(settlement.getPaidAt())
        .status(settlement.getStatus())

        .managerId(manager.getId())
        .managerName(manager.getName())
        .managerPhone(manager.getPhone())
        .managerEmail(manager.getEmail())
        .managerStatus(manager.getStatus())
        .build();
  }

}
