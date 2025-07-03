package com.homeaid.settlement.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.PaymentStatus;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.settlement.domain.Settlement;
import com.homeaid.exception.CustomException;
import com.homeaid.settlement.domain.enumerate.SettlementStatus;
import com.homeaid.settlement.dto.SettlementWithManagerResponseDto;
import com.homeaid.settlement.exception.SettlementErrorCode;
import com.homeaid.settlement.repository.SettlementRepository;
import com.homeaid.settlement.validator.SettlementValidator;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminSettlementServiceImpl implements AdminSettlementService {

  private final SettlementRepository adminSettlementRepository;
  private final PaymentRepository paymentRepository;
  private final SettlementValidator settlementValidator;
  private final ManagerRepository managerRepository;

  // 개별 매니저 주간 정산 생성 - 수동
  @Override
  public Settlement createWeeklySettlementForManager(Long managerId, LocalDate weekStart, LocalDate weekEnd) {
    settlementValidator.validateManagerExists(managerId);
    settlementValidator.validateNotAlreadySettled(managerId, weekStart, weekEnd);

    List<Payment> validPayments = getValidPaymentsForWeek(managerId, weekStart, weekEnd);

    if (validPayments.isEmpty()) {
      throw new CustomException(SettlementErrorCode.NO_PAYMENTS_FOUND);
    }

    return saveSettlement(validPayments, weekStart, weekEnd);
  }

  // 유효한 결제 내역 조회 - 조건: 결제 상태는 PAID, 예약 상태는 COMPLETED
  private List<Payment> getValidPaymentsForWeek(Long managerId, LocalDate weekStart, LocalDate weekEnd) {
    LocalDateTime start = weekStart.atStartOfDay();
    LocalDateTime end = weekEnd.plusDays(1).atStartOfDay(); // end는 다음날 0시까지 포함

    List<Payment> payments = paymentRepository.findAllByReservation_ManagerIdAndPaidAtBetween(managerId, start, end);

    return payments.stream()
        .filter(payment ->
            payment.getStatus() == PaymentStatus.PAID &&
                payment.getReservation().getStatus() == ReservationStatus.COMPLETED
        ).toList();
  }

  private Settlement saveSettlement(List<Payment> validPayments, LocalDate weekStart, LocalDate weekEnd) {
    int totalPaid = validPayments.stream()
        .mapToInt(Payment::getAmount)
        .sum();

    int managerAmount = (int) Math.round(totalPaid * 0.8);
    Long managerId = validPayments.get(0).getReservation().getManagerId();

    Settlement settlement = Settlement.builder()
        .managerId(managerId)
        .totalAmount(totalPaid)
        .managerSettlementPrice(managerAmount)
        .settlementWeekStart(weekStart)
        .settlementWeekEnd(weekEnd)
        .settledAt(LocalDateTime.now())
        .status(SettlementStatus.PENDING)
        .build();

    return adminSettlementRepository.save(settlement);
  }

  // 전체 활성 매니저 정산 일괄 생성 - 스케줄러(WeeklySettlementScheduler)에서 호출
  @Override
  @Transactional
  public void createSettlementsForAllManagers(LocalDate weekStart, LocalDate weekEnd) {
    List<Long> managerIds = settlementValidator.findAllActiveManagerIds(ManagerStatus.ACTIVE);

    for (Long managerId : managerIds) {
      try {
        Settlement settlement = createWeeklySettlementForManager(managerId, weekStart, weekEnd);
        log.info("[정산 생성 성공] managerId={}, settlementId={}", managerId, settlement.getId());
      } catch (CustomException e) {
        log.error("[정산 생성 실패] managerId={}, 이유={}", managerId, e.getMessage(), e);
      }
    }
  }

  @Override
  public List<Settlement> findAll(String status, LocalDate start, LocalDate end) {
    if ((start != null && end == null) || (start == null && end != null)) {
      throw new CustomException(SettlementErrorCode.INVALID_REQUEST); // start 또는 end가 한쪽만 있을 때 예외
    }
    if (start != null && end != null && start.isAfter(end)) {
      throw new CustomException(SettlementErrorCode.INVALID_DATE_RANGE);
    }
    return adminSettlementRepository.findAll();
  }

  @Override
  public Settlement findById(Long settlementId) {
    return adminSettlementRepository.findById(settlementId)
        .orElseThrow(() -> new CustomException(SettlementErrorCode.INVALID_REQUEST));
  }

  @Override
  public List<Settlement> findByManagerId(Long managerId) {
    return adminSettlementRepository.findAllByManagerId(managerId);
  }

  @Override
  public void confirm(Long settlementId) {
    Settlement settlement = settlementValidator.getOrThrow(settlementId);
    settlement.approve();
    adminSettlementRepository.save(settlement);
    log.info("[정산 승인 처리] settlementId={} 상태변경: PENDING -> APPROVED", settlementId);
  }

  @Override
  public void pay(Long settlementId) {
    Settlement settlement = settlementValidator.getOrThrow(settlementId);
    settlement.pay();
    adminSettlementRepository.save(settlement);
    log.info("[정산 지급 처리] settlementId={} 상태변경: APPROVED -> PAID", settlementId);
  }

  // 관리자용 정산 상세 조회 서비스
  @Override
  public SettlementWithManagerResponseDto getSettlementWithManager(Long settlementId) {
    Settlement settlement = findById(settlementId);

    Manager manager = managerRepository.findById(settlement.getManagerId())
        .orElseThrow(() -> new CustomException(SettlementErrorCode.MANAGER_NOT_FOUND));

    List<Payment> payments = getValidPaymentsForWeek(
        manager.getId(),
        settlement.getSettlementWeekStart(),
        settlement.getSettlementWeekEnd()
    );

    return SettlementWithManagerResponseDto.from(settlement, manager, payments);
  }


}