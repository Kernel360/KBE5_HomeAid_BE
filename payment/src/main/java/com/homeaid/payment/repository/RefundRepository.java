package com.homeaid.payment.repository;

import com.homeaid.payment.domain.Refund;
import com.homeaid.payment.domain.enumerate.RefundStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {

  // 회원 ID 기준으로 환불 내역 전체 조회
  List<Refund> findAllByPayment_Reservation_CustomerId(Long customerId);

  // 회원 ID 기준으로 특정 환불 내역 상세 조회
  Optional<Refund> findByIdAndPayment_Reservation_CustomerId(Long refundId, Long customerId);

  // 결제에 대한 중복 환불 요청이 진행 중인지 확인
  boolean existsByPaymentIdAndStatus(Long paymentId, RefundStatus status);

}
