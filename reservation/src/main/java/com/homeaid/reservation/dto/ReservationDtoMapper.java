package com.homeaid.reservation.dto;


import com.homeaid.matching.domain.Matching;
import com.homeaid.reservation.domain.Reservation;
import com.homeaid.reservation.dto.request.ReservationCommand;
import com.homeaid.reservation.dto.response.ManagerReservationResponseDto;
import com.homeaid.reservation.dto.response.ReservationInfo;
import com.homeaid.reservation.dto.request.ReservationRequestDto;
import com.homeaid.reservation.dto.response.ReservationResponseDto;

public interface ReservationDtoMapper {

  ReservationCommand toCommand(ReservationRequestDto reservationRequestDto, Long userId);

  ReservationCommand toCommand(ReservationRequestDto reservationRequestDto, Long userId, Long reservationId);

  ReservationResponseDto toDto(ReservationInfo reservationInfo);

  ReservationResponseDto toDto(Reservation reservation);

  ReservationResponseDto toDto(Reservation reservation, String customerName, String managerName);

  ManagerReservationResponseDto toDto(Reservation reservation, String customerName, Matching matching);

}
