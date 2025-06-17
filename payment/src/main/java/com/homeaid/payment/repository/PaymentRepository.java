package com.homeaid.payment.repository;

import com.homeaid.payment.domain.Payment;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

  List<Payment> findAllByReservation_ManagerIdAndPaidAtBetween(Long managerId, LocalDateTime start, LocalDateTime end);
  boolean existsByReservationId(Long reservationId); // 중복 결제 방지

  @Query("SELECT p FROM Payment p WHERE p.reservation.customerId = :customerId")
  List<Payment> findByCustomerId(@Param("customerId") Long customerId);

}
