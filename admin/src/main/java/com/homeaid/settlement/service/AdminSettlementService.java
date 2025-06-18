package com.homeaid.settlement.service;

import com.homeaid.settlement.domain.Settlement;
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
  void confirm(Long settlementId);

  // 지급 처리
  void pay(Long settlementId);

  Settlement createWeeklySettlementForManager(Long managerId, LocalDate weekStart, LocalDate weekEnd);

}
