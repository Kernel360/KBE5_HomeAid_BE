package com.homeaid.dashboard.service;


import com.homeaid.dashboard.dto.AdminDashboardStatsDto;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.payment.repository.RefundRepository;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.repository.UserRepository;
import com.homeaid.settlement.repository.SettlementRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

  private final PaymentRepository paymentRepository;

  @Override
  public AdminDashboardStatsDto getStats() {
    LocalDate today = LocalDate.now();
    int year = today.getYear();
    int month = today.getMonthValue();
    int day = today.getDayOfMonth();

    // 오늘 매출
    long todayRevenue = paymentRepository.sumPayments(year, month, day);

    // 이번 달 매출
    long monthlyRevenue = paymentRepository.sumPayments(year, month, null);

    // 이번 주 매출 (월요일 ~ 오늘)
    LocalDate monday = today.with(DayOfWeek.MONDAY);
    long weeklyRevenue = 0;
    for (LocalDate date = monday; !date.isAfter(today); date = date.plusDays(1)) {
      weeklyRevenue += paymentRepository.sumPayments(
          date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    // 전체 결제액
    long totalPayments = paymentRepository.sumAllPaymentAmounts();

    // 전체 환불 금액
    long totalRefundAmount = paymentRepository.sumCanceledPayments(year, null, null);

    // 순매출 = 전체 결제 - 환불
    long netRevenue = totalPayments - totalRefundAmount;

    // 환불률 (%)
    double refundRate = totalPayments == 0 ? 0 : (double) totalRefundAmount / totalPayments * 100;

    // 수익 배분
    long platformProfit = Math.round(netRevenue * 0.2);  // 관리자 수익 (20%)
    long managerSettlementAmount = netRevenue - platformProfit;  // 매니저 수익 (80%)

    return AdminDashboardStatsDto.builder()
        .todayRevenue(todayRevenue)
        .weeklyRevenue(weeklyRevenue)
        .monthlyRevenue(monthlyRevenue)
        .netRevenue(netRevenue)
        .totalRefundAmount(totalRefundAmount)
        .refundRate(refundRate)
        .platformProfit(platformProfit)
        .managerSettlementAmount(managerSettlementAmount)
        .build();
  }

}
