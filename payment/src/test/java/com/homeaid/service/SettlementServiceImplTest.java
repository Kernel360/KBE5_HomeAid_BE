package com.homeaid.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.homeaid.domain.Payment;
import com.homeaid.domain.Settlement;
import com.homeaid.dto.response.SettlementResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.SettlementErrorCode;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.repository.PaymentRepository;
import com.homeaid.repository.SettlementRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SettlementServiceImplTest {

  @Mock
  private PaymentRepository paymentRepository;
  @Mock
  private SettlementRepository settlementRepository;
  @Mock
  private ManagerRepository managerRepository;

  @InjectMocks
  private SettlementServiceImpl settlementService;

  public SettlementServiceImplTest() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("주간 정산 생성 성공")
  void createWeeklySettlementForManager_success() {
    // given
    Long managerId = 1L;
    LocalDate weekStart = LocalDate.of(2024, 6, 2);
    LocalDate weekEnd = LocalDate.of(2024, 6, 8);

    given(managerRepository.existsById(managerId)).willReturn(true);

    Payment p1 = mock(Payment.class);
    Payment p2 = mock(Payment.class);
    given(p1.getAmount()).willReturn(10000);
    given(p2.getAmount()).willReturn(5000);

    given(paymentRepository.findAllByReservation_ManagerIdAndPaidAtBetween(
        eq(managerId), any(LocalDateTime.class), any(LocalDateTime.class)
    )).willReturn(List.of(p1, p2));

    Settlement savedSettlement = Settlement.builder()
        .managerId(managerId)
        .totalAmount(15000)
        .managerSettlementPrice(12000)
        .settlementWeekStart(weekStart)
        .settlementWeekEnd(weekEnd)
        .settledAt(LocalDateTime.now())
        .build();
    given(settlementRepository.save(any(Settlement.class))).willReturn(savedSettlement);

    // when
    SettlementResponseDto result = settlementService.createWeeklySettlementForManager(managerId, weekStart, weekEnd);

    // then
    assertThat(result.getManagerId()).isEqualTo(managerId);
    assertThat(result.getTotalAmount()).isEqualTo(15000);
    assertThat(result.getManagerAmount()).isEqualTo(12000); // 80% of 150_000
  }

  @Test
  @DisplayName("정산 생성 시 매니저가 없으면 예외 발생")
  void createWeeklySettlementForManager_managerNotFound() {
    // given
    Long managerId = 99L;
    LocalDate weekStart = LocalDate.of(2024, 5, 26);
    LocalDate weekEnd = LocalDate.of(2024, 6, 1);
    given(managerRepository.existsById(managerId)).willReturn(false);

    // when & then
    assertThatThrownBy(() ->
        settlementService.createWeeklySettlementForManager(managerId, weekStart, weekEnd)
    ).isInstanceOf(CustomException.class)
        .hasMessageContaining(SettlementErrorCode.MANAGER_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("정산 생성 시 결제내역이 없으면 예외 발생")
  void createWeeklySettlementForManager_noPaymentsFound() {
    // given
    Long managerId = 1L;
    LocalDate weekStart = LocalDate.of(2024, 5, 26);
    LocalDate weekEnd = LocalDate.of(2024, 6, 1);

    given(managerRepository.existsById(managerId)).willReturn(true);
    given(paymentRepository.findAllByReservation_ManagerIdAndPaidAtBetween(
        eq(managerId), any(LocalDateTime.class), any(LocalDateTime.class)
    )).willReturn(List.of());

    // when & then
    assertThatThrownBy(() ->
        settlementService.createWeeklySettlementForManager(managerId, weekStart, weekEnd)
    ).isInstanceOf(CustomException.class)
        .hasMessageContaining(SettlementErrorCode.NO_PAYMENTS_FOUND.getMessage());
  }

  @Test
  @DisplayName("findAll: 전체 정산내역 조회")
  void findAll() {
    // given
    Settlement s1 = mock(Settlement.class);
    Settlement s2 = mock(Settlement.class);
    given(settlementRepository.findAll()).willReturn(List.of(s1, s2));

    // when
    List<Settlement> result = settlementService.findAll();

    // then
    assertThat(result).hasSize(2);
  }

  @Test
  @DisplayName("findById: 존재하지 않으면 예외 발생")
  void findById_notFound() {
    // given
    Long id = 123L;
    given(settlementRepository.findById(id)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> settlementService.findById(id))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(SettlementErrorCode.INVALID_REQUEST.getMessage());
  }

  @Test
  @DisplayName("findById: 정상 단건 조회")
  void findById_success() {
    // given
    Long id = 1L;
    Settlement settlement = mock(Settlement.class);
    given(settlementRepository.findById(id)).willReturn(Optional.of(settlement));

    // when
    Settlement result = settlementService.findById(id);

    // then
    assertThat(result).isEqualTo(settlement);
  }

  @Test
  @DisplayName("findByManagerId: 매니저별 정산내역 조회")
  void findByManagerId() {
    // given
    Long managerId = 10L;
    Settlement s1 = mock(Settlement.class);
    Settlement s2 = mock(Settlement.class);
    given(settlementRepository.findAllByManagerId(managerId)).willReturn(List.of(s1, s2));

    // when
    List<Settlement> result = settlementService.findByManagerId(managerId);

    // then
    assertThat(result).hasSize(2);
  }
}