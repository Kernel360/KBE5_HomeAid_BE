package com.homeaid.repository;

import com.homeaid.domain.Payment;
import com.homeaid.domain.PaymentStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

  // payment 에서 reservation - managerId 를 타고 결제일 paidAt 로 필터링
  List<Payment> findAllByReservation_ManagerIdAndPaidAtBetween(Long managerId, LocalDateTime start, LocalDateTime end);


}
