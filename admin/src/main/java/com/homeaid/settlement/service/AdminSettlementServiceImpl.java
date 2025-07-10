package com.homeaid.settlement.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.enumerate.PaymentStatus;
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

  // 관리자가 수동으로 매니저 정산 생성
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

  // 스케줄러 전용: 필요할 때만 정산 생성, 예외로 끊지 않고 상황별로 로그만 기록
  private void createWeeklySettlementIfNecessary(Long managerId, LocalDate weekStart, LocalDate weekEnd) {
    if (!settlementValidator.existsManager(managerId)) {
      log.warn("[정산 스킵] 존재하지 않는 매니저 managerId={}", managerId);
      return;
    }

    if (settlementValidator.isAlreadySettled(managerId, weekStart, weekEnd)) {
      log.info("[정산 스킵] 이미 정산 완료 managerId={} 기간 {} ~ {}", managerId, weekStart, weekEnd);
      return;
    }

    List<Payment> validPayments = getValidPaymentsForWeek(managerId, weekStart, weekEnd);

    if (validPayments.isEmpty()) {
      log.info("[정산 스킵] 결제 내역 없음 managerId={} 기간 {} ~ {}", managerId, weekStart, weekEnd);
      return;
    }

    Settlement settlement = saveSettlement(validPayments, weekStart, weekEnd);
    log.info("[정산 생성 성공] managerId={}, settlementId={}", managerId, settlement.getId());
  }

  // 결제 내역 조회 - 결제상태 PAID + 예약상태 COMPLETED
  private List<Payment> getValidPaymentsForWeek(Long managerId, LocalDate weekStart, LocalDate weekEnd) {
    LocalDateTime start = weekStart.atStartOfDay();
    LocalDateTime end = weekEnd.plusDays(1).atStartOfDay();

    List<Payment> payments = paymentRepository.findAllByReservation_ManagerIdAndPaidAtBetween(managerId, start, end);

    return payments.stream()
        .filter(payment -> payment.getStatus() == PaymentStatus.PAID &&
            payment.getReservation().getStatus() == ReservationStatus.COMPLETED)
        .toList();
  }

  // Settlement 저장 로직
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

  // 전체 활성 매니저 정산 일괄 생성 - 스케줄러에서 호출
  @Override
  @Transactional
  public void createSettlementsForAllManagers(LocalDate weekStart, LocalDate weekEnd) {
    List<Long> managerIds = settlementValidator.findAllActiveManagerIds(ManagerStatus.ACTIVE);

    for (Long managerId : managerIds) {
      try {
        createWeeklySettlementIfNecessary(managerId, weekStart, weekEnd);
      } catch (Exception e) {
        log.error("[정산 스케줄러 실패] managerId={}, 이유={}", managerId, e.getMessage(), e);
      }
    }
  }

  // 전체 조회
  @Override
  public List<Settlement> findAll(String status, LocalDate start, LocalDate end) {
    if ((start != null && end == null) || (start == null && end != null)) {
      throw new CustomException(SettlementErrorCode.INVALID_REQUEST);
    }
    if (start != null && end != null && start.isAfter(end)) {
      throw new CustomException(SettlementErrorCode.INVALID_DATE_RANGE);
    }
    return adminSettlementRepository.findAll();
  }

  // 단건 조회
  @Override
  public Settlement findById(Long settlementId) {
    return adminSettlementRepository.findById(settlementId)
        .orElseThrow(() -> new CustomException(SettlementErrorCode.INVALID_REQUEST));
  }

  // 매니저별 조회
  @Override
  public List<Settlement> findByManagerId(Long managerId) {
    return adminSettlementRepository.findAllByManagerId(managerId);
  }

  // 승인 처리
  @Override
  public Settlement confirm(Long settlementId) {
    Settlement settlement = settlementValidator.getOrThrow(settlementId);
    settlement.approve();
    adminSettlementRepository.save(settlement);
    log.info("[정산 승인 처리] settlementId={} 상태변경: PENDING -> APPROVED", settlementId);
    return settlement;
  }

  // 지급 처리
  @Override
  public Settlement pay(Long settlementId) {
    Settlement settlement = settlementValidator.getOrThrow(settlementId);
    settlement.pay();
    adminSettlementRepository.save(settlement);
    log.info("[정산 지급 처리] settlementId={} 상태변경: APPROVED -> PAID", settlementId);
    return settlement;
  }

  // 관리자용 상세 조회
  @Override
  public SettlementWithManagerResponseDto getSettlementWithManager(Long settlementId) {
    // Settlement 조회
    Settlement settlement = findById(settlementId);

    // Manager 조회
    Manager manager = managerRepository.findById(settlement.getManagerId())
        .orElseThrow(() -> new CustomException(SettlementErrorCode.MANAGER_NOT_FOUND));

    // Settlement 기간 기준으로 결제내역 조회
    List<Payment> payments = getPaymentsForSettlement(settlement);

    return SettlementWithManagerResponseDto.from(settlement, manager, payments);
  }

  private List<Payment> getPaymentsForSettlement(Settlement settlement) {
    LocalDateTime start = settlement.getSettlementWeekStart().atStartOfDay();
    LocalDateTime end = settlement.getSettlementWeekEnd().atTime(23, 59, 59);

    List<Payment> payments = paymentRepository.findAllByReservation_ManagerIdAndPaidAtBetween(
        settlement.getManagerId(), start, end);

    if (payments.isEmpty()) {
      throw new CustomException(SettlementErrorCode.NO_PAYMENTS_FOUND);
    }

    return payments;
  }
}
