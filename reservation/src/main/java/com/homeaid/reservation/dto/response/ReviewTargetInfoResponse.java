package com.homeaid.reservation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewTargetInfoResponse {
    private Long reservationId;
    private Long targetId;

    public static ReviewTargetInfoResponse from(Long targetId, Long reservationId) {
        return ReviewTargetInfoResponse.builder()
                .reservationId(reservationId)
                .targetId(targetId)
                .build();
    }
}
