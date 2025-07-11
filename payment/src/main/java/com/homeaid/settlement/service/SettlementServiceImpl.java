package com.homeaid.settlement.service;

import com.homeaid.exception.CustomException;
import com.homeaid.payment.domain.Payment;
import com.homeaid.payment.dto.response.PaymentResponseDto;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.repository.UserRepository;
import com.homeaid.settlement.domain.Settlement;
import com.homeaid.settlement.dto.response.SettlementPaymentDetailResponseDto;
import com.homeaid.settlement.exception.SettlementErrorCode;
import com.homeaid.settlement.repository.SettlementRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

  private final SettlementRepository settlementRepository;
  private final UserRepository userRepository;
  private final PaymentRepository paymentRepository;

  @Override
  public List<Settlement> getWeeklySettlements(Long managerId, LocalDate startDate) {
    return settlementRepository.findByManagerIdAndSettlementWeekStart(managerId, startDate);
  }

  @Override
  public SettlementPaymentDetailResponseDto getPaymentsForSettlement(Long managerId, Long settlementId) {
    Settlement settlement = settlementRepository.findById(settlementId)
        .orElseThrow(() -> new CustomException(SettlementErrorCode.NO_SETTLEMENT_FOUND));

    validateSettlementFields(settlement);

    if (!settlement.getManagerId().equals(managerId)) {
      throw new CustomException(SettlementErrorCode.UNAUTHORIZED_ACCESS);
    }

    List<Payment> payments = paymentRepository.findAllByReservation_ManagerIdAndPaidAtBetween(
        managerId,
        settlement.getSettlementWeekStart().atStartOfDay(),
        settlement.getSettlementWeekEnd().atTime(23, 59, 59)
    );

    if (payments.isEmpty()) {
      throw new CustomException(SettlementErrorCode.NO_PAYMENTS_FOUND);
    }

    List<PaymentResponseDto> paymentDtos = payments.stream()
        .map(p -> PaymentResponseDto.toDto(p, getCustomerName(p.getReservation().getCustomer().getId())))
        .toList();

    log.info("[정산] managerId={} settlementId={} 결제내역 {}건 반환", managerId, settlementId, paymentDtos.size());

    return SettlementPaymentDetailResponseDto.of(paymentDtos);
  }

  // settlement 필수 필드 유효성 검증 메서드
  private void validateSettlementFields(Settlement settlement) {
    if (settlement.getSettlementWeekStart() == null || settlement.getSettlementWeekEnd() == null) {
      throw new CustomException(SettlementErrorCode.INVALID_REQUEST);
    }
  }

  private String getCustomerName(Long customerId) {
    return userRepository.findById(customerId)
        .map(u -> u.getName())
        .orElse("알 수 없음");
  }

}