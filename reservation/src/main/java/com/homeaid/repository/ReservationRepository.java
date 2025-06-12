package com.homeaid.repository;


import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  Page<Reservation> findAllByCustomerId(Long userId, Pageable pageable);

  Page<Reservation> findAllByManagerId(Long managerId, Pageable pageable);

  @Query("SELECT COUNT(r) FROM Reservation r WHERE DATE(r.createdDate) = CURRENT_DATE")
  long countTodayReservations();

  Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);
}
