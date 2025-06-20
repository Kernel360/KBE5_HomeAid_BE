package com.homeaid.dashboard.service;


import com.homeaid.dashboard.dto.AdminDashboardStatsDto;
import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

   private final UserRepository userRepository;
   private final ManagerRepository managerRepository;
   private final ReservationRepository reservationRepository;
   private final PaymentRepository paymentRepository;

  public AdminDashboardStatsDto getStats() {
    long totalUsers = userRepository.countAllUsers();
    long activeManagers = managerRepository.countActiveManagers();
    long pendingApprovals = managerRepository.countPendingManagers();
    long todayReservations = reservationRepository.countTodayReservations();
    long totalPayments = paymentRepository.countAllPayments();

    return AdminDashboardStatsDto.builder()
        .totalUsers(totalUsers)
        .activeManagers(activeManagers)
        .totalPayments(totalPayments)
        .todayReservations(todayReservations)
        .pendingApprovals(pendingApprovals)
        .build();
  }
}
