package com.homeaid.settlement.service;

import com.homeaid.settlement.domain.Settlement;
import com.homeaid.settlement.dto.SettlementWithManagerResponseDto;
import java.time.LocalDate;
import java.util.List;

public interface AdminSettlementService {

  // 전체 정산 목록 조회 (검색조건)
  List<Settlement> findAll(String status, LocalDate start, LocalDate end);

  // 단건 조회
  Settlement findById(Long settlementId);

  // 매니저별 정산 내역 조회
  List<Settlement> findByManagerId(Long managerId);

  // 승인 처리
  Settlement confirm(Long settlementId);

  // 지급 처리
  Settlement pay(Long settlementId);

  // 개별 매니저의 주간 정산 생성
  Settlement createWeeklySettlementForManager(Long managerId, LocalDate weekStart, LocalDate weekEnd);

  // 모든 활성 매니저의 주간 정산 생성
  void createSettlementsForAllManagers(LocalDate weekStart, LocalDate weekEnd);

  // 관리자 매니저 정산 상세조회
  SettlementWithManagerResponseDto getSettlementWithManager(Long settlementId);
}
