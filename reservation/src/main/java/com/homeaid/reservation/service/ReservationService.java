package com.homeaid.reservation.service;


import com.homeaid.reservation.domain.Reservation;
import com.homeaid.reservation.domain.enumerate.ReservationStatus;
import com.homeaid.reservation.dto.response.ManagerReservationResponseDto;
import com.homeaid.reservation.dto.response.ReservationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationService {


  Reservation createReservation(Reservation reservation, Long userId, Long serviceOptionId);

  ReservationResponseDto getReservation(Long reservationId);

  Reservation updateReservation(Long reservationId, Long userId, Reservation reservation, Long serviceOptionId);

  Page<ReservationResponseDto> getReservations(Pageable pageable, ReservationStatus status);

  void deleteReservation(Long reservationId, Long userId);

  Page<Reservation> getReservationsByCustomer(Long userId, Pageable pageable);

  Page<ManagerReservationResponseDto> getReservationsByManager(Long userId, Pageable pageable);

  Reservation validateReservation(Long reservationId);

  void validateManagerAccess(Reservation reservation, Long managerId);

  void validateUserAccess(Reservation reservation, Long managerId);
}
