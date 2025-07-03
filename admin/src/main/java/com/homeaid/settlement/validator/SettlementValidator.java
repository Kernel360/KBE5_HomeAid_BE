package com.homeaid.settlement.validator;

import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.exception.CustomException;
import com.homeaid.repository.ManagerRepository;
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

  private final ManagerRepository managerRepository;
  private final SettlementRepository settlementRepository;

  // 매니저 존재 여부 확인
  public void validateManagerExists(Long managerId) {
    if (!managerRepository.existsById(managerId)) {
      throw new CustomException(SettlementErrorCode.MANAGER_NOT_FOUND);
    }
  }

  // 이미 해당 주차에 정산 완료 여부 확인
  public void validateNotAlreadySettled(Long managerId, LocalDate weekStart, LocalDate weekEnd) {
    boolean exists = settlementRepository.existsByManagerIdAndSettlementWeekStartAndSettlementWeekEnd(
        managerId, weekStart, weekEnd);
    if (exists) {
      throw new CustomException(SettlementErrorCode.ALREADY_SETTLED);
    }
  }

  // 활동중인 매니저 ID 목록 조회
  public List<Long> findAllActiveManagerIds(ManagerStatus status) {
    return managerRepository.findAllIdsByStatus(status);
  }

  // 정산ID 로 조회하고 없으면 예외 발생
  public Settlement getOrThrow(Long settlementId) {
    return settlementRepository.findById(settlementId)
        .orElseThrow(() -> new CustomException(SettlementErrorCode.INVALID_REQUEST));
  }

}
