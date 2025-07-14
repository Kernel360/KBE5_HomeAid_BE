package com.homeaid.reservation.domain;


import com.homeaid.reservation.domain.enumerate.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationReader {

  Reservation getReservation(Long reservationId);

  Page<Reservation> getReservationByStatus(ReservationStatus status, Pageable pageable);
}
