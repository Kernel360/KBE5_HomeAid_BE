package com.homeaid.settlement.dto;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.settlement.domain.Settlement;
import com.homeaid.settlement.domain.enumerate.SettlementStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "정산 + 매니저 상세 정보 응답 DTO")
public class SettlementWithManagerResponseDto {

  @Schema(description = "정산 ID", example = "1")
  private Long id;

  @Schema(description = "정산 기간 시작일", example = "2025-06-01")
  private LocalDate from;

  @Schema(description = "정산 기간 종료일", example = "2025-06-07")
  private LocalDate to;

  @Schema(description = "총 결제금액", example = "100000")
  private Integer totalAmount;

  @Schema(description = "매니저 정산 금액 (80%)", example = "80000")
  private Integer managerAmount;

  @Schema(description = "관리자 수익 (20%)", example = "20000")
  private Integer adminAmount;

  @Schema(description = "정산 생성일", example = "2025-06-08T10:00:00")
  private LocalDateTime settledAt;

  @Schema(description = "정산 승인일", example = "2025-06-09T11:00:00")
  private LocalDateTime confirmedAt;

  @Schema(description = "정산 지급일", example = "2025-06-10T12:00:00")
  private LocalDateTime paidAt;

  @Schema(description = "정산 상태", example = "APPROVED")
  private SettlementStatus status;

  @Schema(description = "매니저 ID", example = "5")
  private Long managerId;

  @Schema(description = "매니저 이름", example = "홍길동")
  private String managerName;

  @Schema(description = "매니저 전화번호", example = "010-1234-5678")
  private String managerPhone;

  @Schema(description = "매니저 이메일", example = "hong@example.com")
  private String managerEmail;

  @Schema(description = "매니저 상태", example = "ACTIVE")
  private ManagerStatus managerStatus;

  @Schema(description = "정산에 포함된 결제 총 건수", example = "3")
  private int totalPayments;

  @Schema(description = "정산에 포함된 결제 목록")
  private List<PaymentResponseDto> payments;

  public static SettlementWithManagerResponseDto from(Settlement settlement, Manager manager, List<PaymentResponseDto> payments) {
    int actualAmount = payments.stream()
        .mapToInt(p -> p.getNetAmount() != null ? p.getNetAmount() : 0)
        .sum();

    int managerAmount = (int) Math.round(actualAmount * 0.8);
    int adminAmount = actualAmount - managerAmount;

    return SettlementWithManagerResponseDto.builder()
        .id(settlement.getId())
        .from(settlement.getSettlementWeekStart())
        .to(settlement.getSettlementWeekEnd())
        .totalAmount(actualAmount)
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
        .totalPayments(payments.size())
        .payments(payments)
        .build();
  }

}
