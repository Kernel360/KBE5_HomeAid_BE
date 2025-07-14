package com.homeaid.reservation.dto;

import com.homeaid.reservation.dto.request.ReservationCommand;
import com.homeaid.reservation.dto.response.ReservationInfo;
import com.homeaid.reservation.dto.request.ReservationRequestDto;
import com.homeaid.reservation.dto.response.ReservationResponseDto;
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
        .build();
  }


}
