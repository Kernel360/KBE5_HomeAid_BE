package com.homeaid.statistics.provider.impl;

import com.homeaid.repository.MatchingRepository;
import com.homeaid.statistics.dto.MatchingStatsDto;
import com.homeaid.statistics.provider.MatchingStatisticsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingStatisticsProviderImpl implements MatchingStatisticsProvider {

  private final MatchingRepository matchingRepository;

  @Override
  public MatchingStatsDto generate(int year, Integer month, Integer day) {
    long success = matchingRepository.countConfirmedMatchings(year, month, day);
    long fail = matchingRepository.countFailedOrCancelledMatchings(year, month, day);
    long total = matchingRepository.countTotalMatchings(year, month, day);

    return MatchingStatsDto.builder()
        .year(year)
        .month(month)
        .day(day)
        .successCount(success)
        .failCount(fail)
        .totalCount(total)
        .build();
  }
}
