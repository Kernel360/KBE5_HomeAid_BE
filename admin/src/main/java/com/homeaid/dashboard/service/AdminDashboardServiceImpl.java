package com.homeaid.dashboard.service;


import com.homeaid.dashboard.dto.AdminDashboardStatsDto;
import com.homeaid.dashboard.dto.DailyRevenueRefundDto;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.payment.repository.RefundRepository;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.reservation.repository.ReservationRepository;
import com.homeaid.repository.UserRepository;
import com.homeaid.settlement.repository.SettlementRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

  private final PaymentRepository paymentRepository;
  private final RefundRepository refundRepository;

  @Override
  public AdminDashboardStatsDto getStats() {
    LocalDate today = LocalDate.now();
    int year = today.getYear();
    int month = today.getMonthValue();
    int day = today.getDayOfMonth();

    // 매출
    long todayRevenue = paymentRepository.sumPayments(year, month, day);
    long monthlyRevenue = paymentRepository.sumPayments(year, month, null);

    // 주간 매출
    LocalDate monday = today.with(DayOfWeek.MONDAY);
    long weeklyRevenue = 0;
    for (LocalDate date = monday; !date.isAfter(today); date = date.plusDays(1)) {
      weeklyRevenue += paymentRepository.sumPayments(
          date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    // 전체 결제/환불
    long totalPayments = paymentRepository.sumAllPaymentAmounts();
    long totalRefundAmount = paymentRepository.sumCanceledPayments(year, null, null);

    // 순매출, 수익
    long netRevenue = totalPayments - totalRefundAmount;
    double refundRate = totalPayments == 0 ? 0 : (double) totalRefundAmount / totalPayments * 100;
    long platformProfit = Math.round(netRevenue * 0.2);
    long managerSettlementAmount = netRevenue - platformProfit;

    // ✅ 수익률: 플랫폼 수익 / 순매출
    double profitRate = netRevenue == 0 ? 0 : (double) platformProfit / netRevenue * 100;

    // 그래프용
    List<DailyRevenueRefundDto> dailyStats = getLast7DaysRevenueAndRefundStats();

    return AdminDashboardStatsDto.builder()
        .todayRevenue(todayRevenue)
        .weeklyRevenue(weeklyRevenue)
        .monthlyRevenue(monthlyRevenue)
        .totalPayments(totalPayments)
        .netRevenue(netRevenue)
        .totalRefundAmount(totalRefundAmount)
        .refundRate(refundRate)
        .platformProfit(platformProfit)
        .managerSettlementAmount(managerSettlementAmount)
        .profitRate(profitRate)
        .dailyStats(dailyStats)
        .build();
  }


  @Override
  public List<DailyRevenueRefundDto> getLast7DaysRevenueAndRefundStats() {
    List<DailyRevenueRefundDto> result = new ArrayList<>();

    LocalDate today = LocalDate.now();
    for (int i = 6; i >= 0; i--) {
      LocalDate targetDate = today.minusDays(i);

      int year = targetDate.getYear();
      int month = targetDate.getMonthValue();
      int day = targetDate.getDayOfMonth();

      long paymentAmount = paymentRepository.sumPayments(year, month, day);
      long refundAmount = refundRepository.sumRefundsByDate(year, month, day); // ← 이 메서드가 RefundRepository에 있어야 함

      result.add(DailyRevenueRefundDto.builder()
          .date(targetDate.toString()) // "yyyy-MM-dd"
          .paymentAmount(paymentAmount)
          .refundAmount(refundAmount)
          .build());
    }

    return result;
  }

}
