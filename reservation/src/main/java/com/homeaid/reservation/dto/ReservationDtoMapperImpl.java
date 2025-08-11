package com.homeaid.reservation.dto;

import com.homeaid.matching.domain.Matching;
import com.homeaid.reservation.domain.Reservation;
import com.homeaid.reservation.dto.request.ReservationCommand;
import com.homeaid.reservation.dto.response.ManagerReservationResponseDto;
import com.homeaid.reservation.dto.response.ReservationInfo;
import com.homeaid.reservation.dto.request.ReservationRequestDto;
import com.homeaid.reservation.dto.response.ReservationResponseDto;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class ReservationDtoMapperImpl implements ReservationDtoMapper {

  @Override
  public ReservationCommand toCommand(ReservationRequestDto reservationRequestDto, Long userId) {
    if (reservationRequestDto == null) {
      return null;
    }
    return ReservationCommand.builder()
        .userId(userId)
        .requestedDate(reservationRequestDto.getRequestedDate())
        .requestedTime(reservationRequestDto.getRequestedTime())
        .latitude(reservationRequestDto.getLatitude())
        .longitude(reservationRequestDto.getLongitude())
        .duration(reservationRequestDto.getTotalDuration())
        .address(reservationRequestDto.getAddress())
        .addressDetail(reservationRequestDto.getAddressDetail())
        .build();
  }

  @Override
  public ReservationCommand toCommand(ReservationRequestDto reservationRequestDto, Long userId,
      Long reservationId) {
    if (reservationRequestDto == null) {
      return null;
    }
    return ReservationCommand.builder()
        .userId(userId)
        .requestedDate(reservationRequestDto.getRequestedDate())
        .requestedTime(reservationRequestDto.getRequestedTime())
        .latitude(reservationRequestDto.getLatitude())
        .longitude(reservationRequestDto.getLongitude())
        .duration(reservationRequestDto.getTotalDuration())
        .address(reservationRequestDto.getAddress())
        .addressDetail(reservationRequestDto.getAddressDetail())
        .reservationId(reservationId)
        .build();
  }

  @Override
  public ReservationResponseDto toDto(ReservationInfo reservationInfo) {
    if (reservationInfo == null) {
      return null;
    }

    return ReservationResponseDto.builder()
        .reservationId(reservationInfo.getId())
        .status(reservationInfo.getStatus())
        .totalPrice(reservationInfo.getTotalPrice())
        .totalDuration(reservationInfo.getDuration())
        .serviceOptionName(reservationInfo.getItemServiceOptionName())
        .customerId(reservationInfo.getCustomerId())
        .managerId(reservationInfo.getManagerId())
        .requestedDate(reservationInfo.getRequestedDate())
        .requestedTime(reservationInfo.getRequestedTime())
        .address(reservationInfo.getAddress())
        .addressDetail(reservationInfo.getAddressDetail())
        .customerMemo(reservationInfo.getCustomerMemo())
        .matchingStatus(reservationInfo.getMatchingStatus())
        .matchedManagerName(reservationInfo.getManagerName())
        .matchingId(reservationInfo.getMatchingId())
        .build();
  }

  @Override
  public ReservationResponseDto toDto(Reservation reservation) {
    if (reservation == null) {
      return null;
    }

    return ReservationResponseDto.builder()
        .reservationId(reservation.getId())
        .status(reservation.getStatus())
        .totalPrice(reservation.getTotalPrice())
        .totalDuration(reservation.getDuration())
        .serviceOptionName(reservation.getItem().getServiceOptionName())
        .customerId(reservation.getCustomer().getId())
        .managerId(reservation.getManagerId())
        .requestedDate(reservation.getRequestedDate())
        .requestedTime(reservation.getRequestedTime())
        .address(reservation.getAddress())
        .addressDetail(reservation.getAddressDetail())
        .customerMemo(reservation.getCustomerMemo())
        .build();
  }

  @Override
  public ReservationResponseDto toDto(Reservation reservation, String customerName,
      String managerName) {

    if (reservation == null) {
      return null;
    }

    return ReservationResponseDto.builder()
        .reservationId(reservation.getId())
        .status(reservation.getStatus())
        .totalPrice(reservation.getTotalPrice())
        .totalDuration(reservation.getDuration())
        .serviceOptionName(reservation.getItem().getServiceOptionName())
        .startTime(LocalDateTime.of(
            reservation.getRequestedDate(),
            reservation.getRequestedTime()
        ))
        .customerName(customerName)
        .matchedManagerName(managerName)
        .build();
  }

  @Override
  public ManagerReservationResponseDto toDto(Reservation reservation, String customerName,
      Matching matching) {

    if (reservation == null) {
      return null;
    }

    return ManagerReservationResponseDto.builder()
        .reservationId(reservation.getId())
        .matchingId(matching.getId())
        .status(reservation.getStatus())
        .serviceOptionName(reservation.getItem().getServiceOptionName())
        .startTime(LocalDateTime.of(
            reservation.getRequestedDate(),
            reservation.getRequestedTime()
        ))
        .matchingStatus(matching.getStatus())
        .customerName(customerName)
        .build();
  }

}
