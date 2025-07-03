package com.homeaid.settlement.validator;

import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.exception.CustomException;
import com.homeaid.service.ManagerService;
import com.homeaid.settlement.domain.Settlement;
import com.homeaid.settlement.exception.SettlementErrorCode;
import com.homeaid.settlement.repository.SettlementRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SettlementValidator {

  private final SettlementRepository settlementRepository;
  private final ManagerService managerService; // ManagerService 의존성 주입

  // 관리자가 직접 요청할 때 매니저 존재 여부 검증 (예외 발생)
  public void validateManagerExists(Long managerId) {
    managerService.validateManagerExists(managerId);
  }

  // 스케줄러용: 매니저 존재 여부 체크 (예외 없이 boolean 반환)
  public boolean existsManager(Long managerId) {
    return managerService.existsManager(managerId);
  }

  // 관리자가 직접 요청할 때 주차 중복 정산 여부 검증 (예외 발생)
  public void validateNotAlreadySettled(Long managerId, LocalDate weekStart, LocalDate weekEnd) {
    boolean exists = settlementRepository.existsByManagerIdAndSettlementWeekStartAndSettlementWeekEnd(
        managerId, weekStart, weekEnd);
    if (exists) {
      throw new CustomException(SettlementErrorCode.ALREADY_SETTLED);
    }
  }

  // 스케줄러용: 이미 주차 정산 완료 여부 체크 (예외 없이 boolean 반환)
  public boolean isAlreadySettled(Long managerId, LocalDate weekStart, LocalDate weekEnd) {
    return settlementRepository.existsByManagerIdAndSettlementWeekStartAndSettlementWeekEnd(
        managerId, weekStart, weekEnd);
  }

  // 활동중인 매니저 ID 목록 조회
  public List<Long> findAllActiveManagerIds(ManagerStatus status) {
    return managerService.findAllActiveManagerIds(status);
  }

  // Settlement ID로 조회하고 없으면 예외 발생
  public Settlement getOrThrow(Long settlementId) {
    return settlementRepository.findById(settlementId)
        .orElseThrow(() -> new CustomException(SettlementErrorCode.INVALID_REQUEST));
  }
}