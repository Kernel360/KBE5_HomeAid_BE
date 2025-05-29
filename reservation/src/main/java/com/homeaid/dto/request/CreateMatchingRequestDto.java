package com.homeaid.dto.request;

import com.homeaid.domain.Matching;
import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.MatchingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateMatchingRequestDto {

  @NotNull
  private Long reservationId;

  @NotNull
  private Long managerId;

  public static Matching toEntity() {
    return Matching.builder()
        .status(MatchingStatus.REQUESTED)
        .managerStatus(MatchingStatus.REQUESTED)
        .customerStatus(MatchingStatus.REQUESTED)
        .build();
  }

}
