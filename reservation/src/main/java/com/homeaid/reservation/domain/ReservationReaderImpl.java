package com.homeaid.reservation.domain;

import com.homeaid.exception.CustomException;
import com.homeaid.reservation.domain.enumerate.ReservationStatus;
import com.homeaid.reservation.exception.ReservationErrorCode;
import com.homeaid.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationReaderImpl implements ReservationReader {

  private final ReservationRepository reservationRepository;

  @Override
  public Reservation getReservation(Long reservationId) {
    return getReservationById(reservationId);
  }

  @Override
  public Page<Reservation> getReservationByStatus(ReservationStatus status, Pageable pageable) {
    return reservationRepository.findByOptionalStatus(status, pageable);
  }

  private Reservation getReservationById(Long reservationId) {
    return reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));
  }

}
