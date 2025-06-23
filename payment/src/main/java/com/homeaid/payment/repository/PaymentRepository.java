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

  @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PAID'") // 또는 조건 없이 COUNT(p)
  long countAllPayments();

  @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE YEAR(p.paidAt) = :year AND p.status = 'PAID'")
  long sumPaymentsByYear(@Param("year") int year);

  @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE YEAR(p.paidAt) = :year AND p.status IN ('CANCELED', 'REFUNDED', 'PARTIAL_REFUNDED')")
  long sumCanceledPaymentsByYear(@Param("year") int year);

  @Query("SELECT " +
      "COALESCE(SUM(CASE WHEN p.paymentMethod = com.homeaid.payment.domain.PaymentMethod.CARD THEN p.amount ELSE 0 END), 0), " +
      "COALESCE(SUM(CASE WHEN p.paymentMethod = com.homeaid.payment.domain.PaymentMethod.TRANSFER THEN p.amount ELSE 0 END), 0), " +
      "COALESCE(SUM(CASE WHEN p.paymentMethod = com.homeaid.payment.domain.PaymentMethod.CASH THEN p.amount ELSE 0 END), 0) " +
      "FROM Payment p " +
      "WHERE YEAR(p.paidAt) = :year AND MONTH(p.paidAt) = :month AND p.status = com.homeaid.payment.domain.PaymentStatus.PAID")
  Object findPaymentMethodSums(@Param("year") int year, @Param("month") int month);

}
