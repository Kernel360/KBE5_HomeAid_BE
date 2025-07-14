package com.homeaid.reservation.dto.request;

import com.homeaid.reservation.domain.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationCommand {

  private final Long userId;
  private final LocalDate requestedDate;
  private final LocalTime requestedTime;
  private final Long optionId;
  private final Integer duration;
  private final String address;
  private final String addressDetail;
  private final Double latitude;
  private final Double longitude;

  public Reservation toEntity() {
    return Reservation.builder()
        .requestedDate(requestedDate)
        .requestedTime(requestedTime)
        .latitude(latitude)
        .longitude(longitude)
        .duration(duration)
        .address(address)
        .addressDetail(addressDetail)
        .build();
  }
}
