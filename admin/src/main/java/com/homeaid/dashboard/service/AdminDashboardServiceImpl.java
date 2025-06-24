package com.homeaid.dashboard.service;


import com.homeaid.dashboard.dto.AdminDashboardStatsDto;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.repository.UserRepository;
import com.homeaid.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

   private final UserRepository userRepository;
   private final ManagerRepository managerRepository;
   private final ReservationRepository reservationRepository;
   private final PaymentRepository paymentRepository;
  private final SettlementRepository settlementRepository;

  public AdminDashboardStatsDto getStats() {
    long totalUsers = userRepository.countAllUsers();
    long activeManagers = managerRepository.countActiveManagers();
    long pendingApprovals = managerRepository.countPendingManagers();
    long todayReservations = reservationRepository.countTodayReservations();
    long totalPayments = paymentRepository.countAllPayments();
    long totalPaymentAmount = paymentRepository.sumAllPaymentAmounts();
    long totalSettlementAmount = settlementRepository.sumAllSettlementAmounts();

    return AdminDashboardStatsDto.builder()
        .totalUsers(totalUsers)
        .activeManagers(activeManagers)
        .totalPayments(totalPayments)
        .todayReservations(todayReservations)
        .pendingApprovals(pendingApprovals)
        .totalPaymentAmount(totalPaymentAmount)
        .totalSettlementAmount(totalSettlementAmount)
        .build();
  }
}
