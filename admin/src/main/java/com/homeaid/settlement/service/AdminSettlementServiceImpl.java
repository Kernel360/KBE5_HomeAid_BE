package com.homeaid.settlement.service;

import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.domain.PaymentStatus;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.settlement.domain.Settlement;
import com.homeaid.exception.CustomException;
import com.homeaid.settlement.exception.SettlementErrorCode;
import com.homeaid.settlement.repository.SettlementRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminSettlementServiceImpl implements AdminSettlementService {

  private final SettlementRepository adminSettlementRepository;
  private final PaymentRepository paymentRepository;
  private final ManagerRepository managerRepository;

  @Override
  public Settlement createWeeklySettlementForManager(Long managerId, LocalDate weekStart, LocalDate weekEnd) {
    validateManagerExists(managerId);
    List<Payment> validPayments = getValidPaymentsForWeek(managerId, weekStart, weekEnd);

    if (validPayments.isEmpty()) {
      throw new CustomException(SettlementErrorCode.NO_PAYMENTS_FOUND);
    }

    return saveSettlement(validPayments, weekStart, weekEnd);
  }

  private void validateManagerExists(Long managerId) {
    if (!managerRepository.existsById(managerId)) {
      throw new CustomException(SettlementErrorCode.MANAGER_NOT_FOUND);
    }
  }

  private List<Payment> getValidPaymentsForWeek(Long managerId, LocalDate weekStart, LocalDate weekEnd) {
    LocalDateTime start = weekStart.atStartOfDay();
    LocalDateTime end = weekEnd.plusDays(1).atStartOfDay();

    List<Payment> payments = paymentRepository.findAllByReservation_ManagerIdAndPaidAtBetween(managerId, start, end);

    // TODO : 정산 가능한 결제만 필터링 (결제완료 + 서비스완료) 수정 필요
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
        .build();

    return adminSettlementRepository.save(settlement);
  }

  @Override
  public List<Settlement> findAll(String status, LocalDate start, LocalDate end) {
    if (start != null && end != null) {
      return adminSettlementRepository.findBySettlementWeekStartBetween(start, end);
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
    Settlement settlement = findById(settlementId);
    if (settlement.getConfirmedAt() != null) {
      throw new CustomException(SettlementErrorCode.ALREADY_CONFIRMED);
    }
    settlement.setConfirmedAt(LocalDateTime.now());
    adminSettlementRepository.save(settlement);
  }

  @Override
  public void pay(Long settlementId) {
    Settlement settlement = findById(settlementId);
    if (settlement.getConfirmedAt() == null) {
      throw new CustomException(SettlementErrorCode.NOT_CONFIRMED);
    }
    if (settlement.getPaidAt() != null) {
      throw new CustomException(SettlementErrorCode.ALREADY_PAID);
    }
    settlement.setPaidAt(LocalDateTime.now());
    adminSettlementRepository.save(settlement);
  }

}