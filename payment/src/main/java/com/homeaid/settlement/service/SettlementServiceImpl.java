package com.homeaid.settlement.service;

import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.settlement.domain.Settlement;
import com.homeaid.exception.CustomException;
import com.homeaid.settlement.exception.SettlementErrorCode;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.settlement.repository.SettlementRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

  private final PaymentRepository paymentRepository;
  private final SettlementRepository settlementRepository;
  private final ManagerRepository managerRepository;

  @Override
  public Settlement createWeeklySettlementForManager(Long managerId, LocalDate weekStart, LocalDate weekEnd) {

    if (!managerRepository.existsById(managerId)) {
      throw new CustomException(SettlementErrorCode.MANAGER_NOT_FOUND);
    }

    LocalDateTime start = weekStart.atStartOfDay();
    LocalDateTime end = weekEnd.plusDays(1).atStartOfDay();

    // 결제내역 조회
    List<Payment> payments = paymentRepository.findAllByReservation_ManagerIdAndPaidAtBetween(managerId, start, end);

    // 결제 내역이 없다면 예외 발생 (정산 불가)
    if (payments.isEmpty()) {
      throw new CustomException(SettlementErrorCode.NO_PAYMENTS_FOUND);
    }

    // 결제 금액 합산 (총 결제액)
    int totalPaid = payments.stream()
        .mapToInt(Payment::getAmount)
        .sum();

    // 매니저 몫(80%)만 계산해서 저장
    int managerAmount = (int) Math.round(totalPaid * 0.8);
    // int adminAmount = totalPaid - managerAmount;

    // Settlement
    Settlement settlement = Settlement.builder()
        .managerId(managerId)
        .totalAmount(totalPaid)
        .managerSettlementPrice(managerAmount) // DB 에는 80%만 저장!
        // .adminSettlementPrice(adminAmount) //관리자 내역 db X
        .settlementWeekStart(weekStart)
        .settlementWeekEnd(weekEnd)
        .settledAt(LocalDateTime.now())
        .build();

    // 실제로 정산 내역 저장
    settlementRepository.save(settlement);

    return settlement;
  }

  // 전체
  public List<Settlement> findAll() {
    return settlementRepository.findAll();
  }

  // 단건
  public Settlement findById(Long id) {
    return settlementRepository.findById(id)
        .orElseThrow(() -> new CustomException(SettlementErrorCode.INVALID_REQUEST));
  }

  // 매니저별 전체
  public List<Settlement> findByManagerId(Long managerId) {
    return settlementRepository.findAllByManagerId(managerId);
  }

}