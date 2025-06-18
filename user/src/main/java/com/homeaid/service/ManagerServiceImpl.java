package com.homeaid.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.ManagerAvailability;
import com.homeaid.domain.ManagerServiceOption;
import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.domain.enumerate.Weekday;
import com.homeaid.dto.request.ManagerDetailInfoRequestDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.ManagerAvailabilityRepository;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.repository.ManagerServiceOptionRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

  private final ManagerRepository managerRepository;
  private final ManagerAvailabilityRepository managerAvailabilityRepository;
  private final ManagerServiceOptionRepository managerServiceOptionRepository;

  @Override
  @Transactional
  public void saveManagerDetailInfo(Long managerId, ManagerDetailInfoRequestDto dto) {
    Manager manager = managerRepository.findById(managerId)
        .orElseThrow(() -> new CustomException(UserErrorCode.MANAGER_NOT_FOUND));

    // 1. 선호 기능 저장
    List<ManagerServiceOption> preferences = dto.getPreferenceIds().stream()
        .map(preferenceId -> ManagerServiceOption.builder()
            .manager(manager)
            .serviceOptionId(preferenceId)
            .build())
        .collect(Collectors.toList());
    managerServiceOptionRepository.saveAll(preferences);

    // 2. 가능한 조건 저장 (요일 별로)
    List<ManagerAvailability> availableConditions = dto.getAvailableDays().stream()
        .map(day -> ManagerAvailability.builder()
            .manager(manager)
            .weekday(convertToWeekday(day))
            .latitude(dto.getLatitude())
            .longitude(dto.getLongitude())
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .build())
        .collect(Collectors.toList());
    managerAvailabilityRepository.saveAll(availableConditions);
  }

  private Weekday convertToWeekday(int day) {
    return Weekday.values()[day - 1]; // 1~7 → 0~6
  }
}
