package com.homeaid.reservation.dto.response;

import com.homeaid.reservation.domain.Reservation;
import com.homeaid.reservation.domain.enumerate.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;

@Getter
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

  public ReservationInfo(Reservation reservation) {
    this.id = reservation.getId();
    this.requestedDate = reservation.getRequestedDate();
    this.requestedTime = reservation.getRequestedTime();
    this.totalPrice = reservation.getTotalPrice();
    this.duration = reservation.getDuration();
    this.status = reservation.getStatus();
    this.address = reservation.getAddress();
    this.addressDetail = reservation.getAddressDetail();
    this.latitude = reservation.getLatitude();
    this.longitude = reservation.getLongitude();
    this.customerId = reservation.getCustomer().getId();
    this.managerId = reservation.getManagerId();
    this.itemBasePrice = reservation.getItem().getBasePrice();
    this.itemServiceOptionName = reservation.getItem().getServiceOptionName();
    this.customerMemo = reservation.getCustomerMemo();
    this.createdDate = reservation.getCreatedDate();
    this.modifiedDate = reservation.getModifiedDate();
    this.deletedDate = reservation.getDeletedDate();
  }

}