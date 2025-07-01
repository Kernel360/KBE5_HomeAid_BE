package com.homeaid.repository;

import com.homeaid.domain.ServiceIssue;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceIssueRepository extends JpaRepository<ServiceIssue, Long> {

  // 이슈 중복 등록 확인
  boolean existsByReservationId(Long reservationId);

  // 예약 ID로 이슈 조회
  @Query("SELECT si FROM ServiceIssue si " +
      "JOIN FETCH si.reservation r " +
      "WHERE si.reservation.id = :reservationId")
  Optional<ServiceIssue> findByReservationId(@Param("reservationId") Long reservationId);

  // 매니저가 접근 가능한 이슈인지 확인
  @Query("SELECT si FROM ServiceIssue si " +
      "JOIN FETCH si.reservation r " +
      "WHERE si.id = :issueId " +
      "AND r.managerId = :managerId ")
  Optional<ServiceIssue> findByIdAndManagerAccess(@Param("issueId") Long issueId,
      @Param("managerId") Long managerId);

  @Query("SELECT si FROM ServiceIssue si LEFT JOIN FETCH si.images WHERE si.id = :issueId")
  Optional<ServiceIssue> findByIdWithImages(@Param("issueId") Long issueId);
}