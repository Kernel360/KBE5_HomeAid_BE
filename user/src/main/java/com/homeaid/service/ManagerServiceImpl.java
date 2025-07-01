package com.homeaid.service;

import com.homeaid.common.enumerate.DocumentType;
import com.homeaid.common.request.UploadFileParam;
import com.homeaid.domain.Manager;
import com.homeaid.domain.ManagerAvailability;
import com.homeaid.domain.ManagerDocument;
import com.homeaid.domain.ManagerPreferRegion;
import com.homeaid.domain.ManagerServiceOption;
import com.homeaid.domain.enumerate.Weekday;
import com.homeaid.dto.request.ManagerDetailInfoRequestDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.ManagerAvailabilityRepository;
import com.homeaid.repository.ManagerDocumentRepository;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.repository.ManagerServiceOptionRepository;
import com.homeaid.util.RegionValidator;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

  private final ManagerRepository managerRepository;
  private final ManagerAvailabilityRepository managerAvailabilityRepository;
  private final ManagerServiceOptionRepository managerServiceOptionRepository;

  private final RegionValidator regionValidator;
  private final ManagerDocumentRepository managerDocumentRepository;
  private final ManagerDocumentService managerDocumentService;

  private static final String S3_PACKAGE_NAME_ID = "USERS/MANAGERS/ID_FILE/";
  private static final String S3_PACKAGE_NAME_CRIMINAL = "USERS/MANAGERS/CRIMINAL_FILE/";
  private static final String S3_PACKAGE_NAME_HEALTH = "USERS/MANAGERS/HEALTH_FILE/";

  @Override
  @Transactional
  public void saveManagerDetailInfo(Long managerId, ManagerDetailInfoRequestDto dto) {
    Manager manager = getManagerById(managerId);

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

          validateTimeRange(availabilityDto.getStartTime(), availabilityDto.getEndTime());

          // 근무 가능 정보 생성
          ManagerAvailability availability = ManagerAvailability.builder()
              .manager(manager)
              .weekday(weekday)
              .startTime(availabilityDto.getStartTime())
              .endTime(availabilityDto.getEndTime())
              .build();

          // 선호 지역 설정
          List<ManagerPreferRegion> regions = availabilityDto.getPreferRegions().stream()
              .map(regionDto -> {
                registerAddress(regionDto.getSido(), regionDto.getSigungu());

                return ManagerPreferRegion.builder()
                    .sido(regionDto.getSido())
                    .sigungu(regionDto.getSigungu())
                    .build();
              })
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

  @Override
  public Manager uploadManagerFiles(Long managerId, MultipartFile idFile, MultipartFile criminalFile,
      MultipartFile healthFile)
      throws IOException {

    Manager manager = getManagerById(managerId);

    List<UploadFileParam> fileParams = List.of(
        new UploadFileParam(DocumentType.ID_CARD, S3_PACKAGE_NAME_ID, idFile),
        new UploadFileParam(DocumentType.CRIMINAL_RECORD, S3_PACKAGE_NAME_CRIMINAL, criminalFile),
        new UploadFileParam(DocumentType.HEALTH_CERTIFICATE, S3_PACKAGE_NAME_HEALTH, healthFile)
    );

    List<ManagerDocument> documents = managerDocumentService.upload(manager, fileParams);
    managerDocumentRepository.saveAll(documents);

    return manager;
  }

  @Override
  public Manager updateManagerFiles(Long managerId, MultipartFile idFile, MultipartFile criminalFile,
      MultipartFile healthFile) throws IOException {
    Manager manager = getManagerById(managerId);

    List<UploadFileParam> fileParams = List.of(
            new UploadFileParam(DocumentType.ID_CARD, S3_PACKAGE_NAME_ID, idFile),
            new UploadFileParam(DocumentType.CRIMINAL_RECORD, S3_PACKAGE_NAME_CRIMINAL, criminalFile),
            new UploadFileParam(DocumentType.HEALTH_CERTIFICATE, S3_PACKAGE_NAME_HEALTH, healthFile)
        ).stream()
        .filter(param -> param.file() != null && !param.file().isEmpty())
        .toList();

    if (fileParams.isEmpty()) {
      return manager;
    }

    List<ManagerDocument> updatedDocuments = managerDocumentService.update(manager, fileParams);
    managerDocumentRepository.saveAll(updatedDocuments);

    return manager;
  }

  @Override
  public Manager getManagerFiles(Long managerId) {
    Manager manager = getManagerById(managerId);

    return manager;
  }

  private void registerAddress(String sido, String sigungu) {
    if (!regionValidator.isValid(sido, sigungu)) {
      throw new CustomException(UserErrorCode.INVALID_MANAGER_REGION);
    }
  }

  private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
    LocalTime min = LocalTime.of(6, 0);   // 06:00
    LocalTime max = LocalTime.of(22, 0);  // 22:00

    if (startTime == null || endTime == null) {
      throw new CustomException(UserErrorCode.INVALID_WORKTIME);
    }

    if (startTime.isBefore(min) || endTime.isAfter(max)) {
      throw new CustomException(UserErrorCode.INVALID_WORKTIME);
    }

    if (!startTime.isBefore(endTime)) {
      throw new CustomException(UserErrorCode.INVALID_WORKTIME_ORDER);
    }
  }

  @Override
  public Manager getManagerById(Long id) {
    return managerRepository.findById(id)
        .orElseThrow(() -> new CustomException(UserErrorCode.MANAGER_NOT_FOUND));
  }

}
