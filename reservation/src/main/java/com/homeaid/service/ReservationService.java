package com.homeaid.service;


import com.homeaid.domain.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationService {


  Reservation createReservation(Reservation reservation, Long serviceSubOptionId);

  Reservation getReservation(Long reservationId);

  Reservation updateReservation(Long reservationId, Long userId, Reservation reservation, Long serviceSubOptionId);

  void deleteReservation(Long id);

  Page<Reservation> getReservations(Pageable pageable);

  void deleteReservation(Long reservationId, Long userId);
}
