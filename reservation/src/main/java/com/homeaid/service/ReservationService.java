package com.homeaid.service;


import com.homeaid.domain.Reservation;

public interface ReservationService {


  Reservation createReservation(Reservation reservation, Long serviceSubOptionId);

  Reservation getReservation(Long reservationId);

  Reservation updateReservation(Long reservationId, Long userId, Reservation reservation, Long serviceSubOptionId);

  void deleteReservation(Long reservationId, Long userId);
}
