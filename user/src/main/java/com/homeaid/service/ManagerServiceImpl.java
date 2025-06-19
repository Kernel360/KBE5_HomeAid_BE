package com.homeaid.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.ManagerAvailability;
import com.homeaid.domain.ManagerPreferRegion;
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

    // 1. 선호 서비스 옵션 저장
    List<ManagerServiceOption> preferences = dto.getPreferenceIds().stream()
        .map(preferenceId -> ManagerServiceOption.builder()
            .manager(manager)
            .serviceOptionId(preferenceId)
            .build())
        .collect(Collectors.toList());
    managerServiceOptionRepository.saveAll(preferences);

    // 2. 가능한 요일 조건 및 선호 지역 저장
    List<ManagerAvailability> availabilities = dto.getAvailabilities().stream()
        .map(availabilityDto -> {
          // 요일 설정
          Weekday weekday = convertToWeekday(availabilityDto.getWeekday());

          // 근무 가능 정보 생성
          ManagerAvailability availability = ManagerAvailability.builder()
              .manager(manager)
              .weekday(weekday)
              .startTime(availabilityDto.getStartTime())
              .endTime(availabilityDto.getEndTime())
              .build();

          // 선호 지역 설정
          List<ManagerPreferRegion> regions = availabilityDto.getPreferRegions().stream()
              .map(regionDto -> ManagerPreferRegion.builder()
                  .sido(regionDto.getSido())
                  .sigungu(regionDto.getSigungu())
                  .build())
              .toList();

          // 연관관계 연결
          regions.forEach(availability::addPreferRegion);

          return availability;
        })
        .toList();

    managerAvailabilityRepository.saveAll(availabilities);
  }

  private Weekday convertToWeekday(int day) {
    return Weekday.values()[day - 1]; // 1~7 → 0~6
  }
}
