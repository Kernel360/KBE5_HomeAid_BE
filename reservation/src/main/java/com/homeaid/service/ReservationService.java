package com.homeaid.service;


import com.homeaid.domain.Reservation;

public interface ReservationService {


  Reservation createReservation(Reservation reservation, Long serviceSubOptionId);

  Reservation getReservation(Long id);

  Reservation updateReservation(Long id, Reservation reservation, Long serviceSubOptionId);

  void deleteReservation(Long id);
}
