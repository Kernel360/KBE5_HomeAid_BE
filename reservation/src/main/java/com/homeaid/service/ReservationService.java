package com.homeaid.service;


import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.dto.response.ReservationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationService {


  Reservation createReservation(Reservation reservation, Long serviceOptionId);

  ReservationResponseDto getReservation(Long reservationId);

  Reservation updateReservation(Long reservationId, Long userId, Reservation reservation, Long serviceOptionId);

  Page<ReservationResponseDto> getReservations(Pageable pageable, ReservationStatus status);

  void deleteReservation(Long reservationId, Long userId);

  Page<Reservation> getReservationsByCustomer(Long userId, Pageable pageable);

  Page<Reservation> getReservationsByManager(Long userId, Pageable pageable);


}
