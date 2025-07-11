package com.homeaid.statistics.provider.impl;


import com.homeaid.reservation.repository.ReservationRepository;
import com.homeaid.statistics.dto.ReservationStatsDto;
import com.homeaid.statistics.provider.ReservationStatisticsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationStatisticsProviderImpl implements ReservationStatisticsProvider {

  private final ReservationRepository reservationRepository;

  @Override
  public ReservationStatsDto generate(int year, Integer month, Integer day) {
    long reservationCount = reservationRepository.countReservations(year, month, day);
    long cancelledCount = reservationRepository.countCancelledReservations(year, month, day);
    Double avgMinutes = reservationRepository.getAverageProcessingMinutes(year, month, day);
    long completedCount = reservationRepository.countCompletedReservations(year, month, day);

    double successRate = (reservationCount > 0)
        ? (completedCount * 100.0 / reservationCount)
        : 0.0;

    return ReservationStatsDto.builder()
        .year(year).month(month).day(day)
        .reservationCount(reservationCount)
        .cancelledCount(cancelledCount)
        .avgProcessingMinutes(avgMinutes != null ? avgMinutes : 0.0)
        .reservationSuccessRate(successRate)
        .build();
  }
}
