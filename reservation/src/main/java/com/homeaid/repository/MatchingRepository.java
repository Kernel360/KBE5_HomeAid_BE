package com.homeaid.repository;

import com.homeaid.domain.Matching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingRepository extends JpaRepository<Matching, Long> {

  @Query("SELECT COUNT(m) FROM Matching m WHERE m.reservation.id = :reservationId")
  int countByReservationId(@Param("reservationId") Long reservationId);

  Page<Matching> findAllByManagerId(Long managerId, Pageable pageable);
}
