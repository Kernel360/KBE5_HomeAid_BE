package com.homeaid.statistics.provider.impl;

import com.homeaid.repository.UserRepository;
import com.homeaid.statistics.dto.UserStatsDto;
import com.homeaid.statistics.provider.UserStatisticsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatisticsProviderImpl implements UserStatisticsProvider {

  private final UserRepository userRepository;

  @Override
  public UserStatsDto generate(int year, Integer month, Integer day) {
    return UserStatsDto.builder()
        .year(year)
        .month(month)
        .day(day)
        .signupCount(userRepository.countSignUps(year, month, day))
        .totalUsers(userRepository.count())
        .withdrawCount(userRepository.countWithdrawn(year, month, day))
        .build();
  }

}
