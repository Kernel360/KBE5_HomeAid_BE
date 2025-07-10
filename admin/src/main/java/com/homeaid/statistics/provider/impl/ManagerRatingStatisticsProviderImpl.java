package com.homeaid.statistics.provider.impl;

import com.homeaid.repository.ReviewRepository;
import com.homeaid.statistics.dto.ManagerRatingStatsDto;
import com.homeaid.statistics.provider.ManagerRatingStatisticsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerRatingStatisticsProviderImpl implements ManagerRatingStatisticsProvider {

  private final ReviewRepository reviewRepository;

  @Override
  public ManagerRatingStatsDto generate(int year, Integer month, Integer day) {
    Double avg = reviewRepository.findAvgRatingForManagers(year, month, day);
    Long count = reviewRepository.countReviewsForManagers(year, month, day);

    return ManagerRatingStatsDto.builder()
        .year(year)
        .month(month)
        .day(day)
        .avgRating(avg != null ? avg : 0.0)
        .reviewCount(count != null ? count : 0L)
        .build();
  }
}
