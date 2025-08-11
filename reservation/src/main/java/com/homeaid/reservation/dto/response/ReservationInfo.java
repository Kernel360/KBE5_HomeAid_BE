package com.homeaid.reservation.dto.response;

import com.homeaid.matching.controller.enumerate.MatchingStatus;
import com.homeaid.reservation.domain.Reservation;
import com.homeaid.reservation.domain.enumerate.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ReservationInfo {

  private final Long id;
  private final LocalDate requestedDate;
  private final LocalTime requestedTime;
  private final Integer totalPrice;
  private final Integer duration;
  private final ReservationStatus status;
  private final String address;
  private final String addressDetail;
  private final Double latitude;
  private final Double longitude;
  private final Long customerId;
  private final Long managerId;
  private final Integer itemBasePrice;
  private final String itemServiceOptionName;
  private final String customerMemo;
  private final LocalDateTime createdDate;
  private final LocalDateTime modifiedDate;
  private final LocalDateTime deletedDate;
  private final MatchingStatus matchingStatus;
  private final String managerName;
  private final Long matchingId;

  public static ReservationInfo toInfo(Reservation reservation) {
    return ReservationInfo.builder()
        .id(reservation.getId())
        .requestedDate(reservation.getRequestedDate())
        .requestedTime(reservation.getRequestedTime())
        .totalPrice(reservation.getTotalPrice())
        .duration(reservation.getDuration())
        .status(reservation.getStatus())
        .address(reservation.getAddress())
        .addressDetail(reservation.getAddressDetail())
        .latitude(reservation.getLatitude())
        .longitude(reservation.getLongitude())
        .customerId(reservation.getCustomer().getId())
        .managerId(reservation.getManagerId())
        .itemBasePrice(reservation.getItem().getBasePrice())
        .itemServiceOptionName(reservation.getItem().getServiceOptionName())
        .customerMemo(reservation.getCustomerMemo())
        .createdDate(reservation.getCreatedDate())
        .modifiedDate(reservation.getModifiedDate())
        .deletedDate(reservation.getDeletedDate())
        .matchingStatus(null)
        .managerName(null)
        .matchingId(null)
        .build();
  }

  public static ReservationInfo toInfo(Reservation reservation, MatchingStatus matchingStatus, String managerName, Long matchingId) {
    return ReservationInfo.builder()
        .id(reservation.getId())
        .requestedDate(reservation.getRequestedDate())
        .requestedTime(reservation.getRequestedTime())
        .totalPrice(reservation.getTotalPrice())
        .duration(reservation.getDuration())
        .status(reservation.getStatus())
        .address(reservation.getAddress())
        .addressDetail(reservation.getAddressDetail())
        .latitude(reservation.getLatitude())
        .longitude(reservation.getLongitude())
        .customerId(reservation.getCustomer().getId())
        .managerId(reservation.getManagerId())
        .itemBasePrice(reservation.getItem().getBasePrice())
        .itemServiceOptionName(reservation.getItem().getServiceOptionName())
        .customerMemo(reservation.getCustomerMemo())
        .createdDate(reservation.getCreatedDate())
        .modifiedDate(reservation.getModifiedDate())
        .deletedDate(reservation.getDeletedDate())
        .matchingStatus(matchingStatus)
        .managerName(managerName)
        .matchingId(matchingId)
        .build();
  }

}