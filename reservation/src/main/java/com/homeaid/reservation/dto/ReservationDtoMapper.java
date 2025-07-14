package com.homeaid.reservation.dto;


import com.homeaid.reservation.dto.request.ReservationCommand;
import com.homeaid.reservation.dto.response.ReservationInfo;
import com.homeaid.reservation.dto.request.ReservationRequestDto;
import com.homeaid.reservation.dto.response.ReservationResponseDto;

public interface ReservationDtoMapper {

  ReservationCommand toCommand(ReservationRequestDto reservationRequestDto, Long userId);

  ReservationCommand toCommand(ReservationRequestDto reservationRequestDto, Long userId, Long reservationId);

  ReservationResponseDto toDto(ReservationInfo reservationInfo);
}
