package com.homeaid.payment.repository;

import com.homeaid.payment.domain.Refund;
import com.homeaid.payment.domain.enumerate.RefundStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RefundRepository extends JpaRepository<Refund, Long> {

  // 회원 ID 기준으로 환불 내역 전체 조회
  List<Refund> findAllByPayment_Reservation_CustomerId(Long customerId);

  // 회원 ID 기준으로 특정 환불 내역 상세 조회
  Optional<Refund> findByIdAndPayment_Reservation_CustomerId(Long refundId, Long customerId);

  // 결제에 대한 중복 환불 요청이 진행 중인지 확인
  boolean existsByPaymentIdAndStatus(Long paymentId, RefundStatus status);

  // Refund 테이블에서 Payment로 조인 → 다시 Payment의 Reservation → Reservation의 customerId를 기준으로 조회
  Page<Refund> findByPayment_Reservation_CustomerId(Long customerId, Pageable pageable);

  // 관리자가 환불을 시도할 때 중복 환불 여부 체크
  boolean existsByPayment_IdAndStatusIn(Long paymentId, List<RefundStatus> statuses);

  List<Refund> findAllByPaymentId(Long paymentId);

  // 관리자 대시보드
  @Query("SELECT COALESCE(SUM(r.refundAmount), 0) FROM Refund r WHERE r.status = 'COMPLETED'")
  long sumAllRefundAmounts();
}
