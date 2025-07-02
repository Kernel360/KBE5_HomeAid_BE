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
    // 매니저 존재 검증
    settlementValidator.validateManagerExists(managerId);
    // 이미 해당 기간에 정산 생성 여부 검증
    settlementValidator.validateNotAlreadySettled(managerId, weekStart, weekEnd);

    // 해당 주차 결제 내역 조회
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

  // 정산 엔티티 생성 및 저장 - 상태는 PENDING으로 저장
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
        log.warn("[정산 생성 실패] managerId={}, 이유={}", managerId, e.getMessage());
      }
    }
  }

  // 전체 정산 조회
  @Override
  public List<Settlement> findAll(String status, LocalDate start, LocalDate end) {
    if (start != null && end != null) {
      return adminSettlementRepository.findBySettlementWeekStartBetween(start, end);
    }
    return adminSettlementRepository.findAll();
  }

  // 단건 정산 조회
  @Override
  public Settlement findById(Long settlementId) {
    return adminSettlementRepository.findById(settlementId)
        .orElseThrow(() -> new CustomException(SettlementErrorCode.INVALID_REQUEST));
  }

  // 매니저별 정산 내역 조회
  @Override
  public List<Settlement> findByManagerId(Long managerId) {
    return adminSettlementRepository.findAllByManagerId(managerId);
  }


  // 정산 승인 처리 - PENDING인 경우에만 승인
  @Override
  public void confirm(Long settlementId) {
    Settlement settlement = findById(settlementId);
    if (settlement.getStatus() != SettlementStatus.PENDING) {
      throw new CustomException(SettlementErrorCode.ALREADY_CONFIRMED);
    }
    settlement.setConfirmedAt(LocalDateTime.now());
    settlement.setStatus(SettlementStatus.APPROVED);
    adminSettlementRepository.save(settlement);
  }

  // 정산 지급 처리 - APPROVED인 경우에만 지급
  @Override
  public void pay(Long settlementId) {
    Settlement settlement = findById(settlementId);
    if (settlement.getStatus() != SettlementStatus.APPROVED) {
      throw new CustomException(SettlementErrorCode.NOT_CONFIRMED);
    }
    settlement.setPaidAt(LocalDateTime.now());
    settlement.setStatus(SettlementStatus.PAID);
    adminSettlementRepository.save(settlement);
  }

  @Override
  public SettlementWithManagerResponseDto getSettlementWithManager(Long settlementId) {
    // 정산 정보 조회 (없으면 예외 발생)
    Settlement settlement = findById(settlementId);

    // 매니저 정보 조회 (없으면 예외 발생)
    Manager manager = managerRepository.findById(settlement.getManagerId())
        .orElseThrow(() -> new CustomException(SettlementErrorCode.MANAGER_NOT_FOUND));

    // Settlement + Manager 결합 DTO 생성
    return SettlementWithManagerResponseDto.from(settlement, manager);
  }


}