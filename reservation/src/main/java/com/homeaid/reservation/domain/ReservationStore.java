package com.homeaid.reservation.domain;

import com.homeaid.reservation.dto.request.ReservationCommand;

public interface ReservationStore {

  Reservation save(ReservationCommand reservationCommand);

  Reservation update(ReservationCommand reservationCommand);

}
