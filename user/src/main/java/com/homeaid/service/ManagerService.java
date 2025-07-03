package com.homeaid.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.dto.request.ManagerDetailInfoRequestDto;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ManagerService {

  void saveManagerDetailInfo(Long managerId, ManagerDetailInfoRequestDto requestDto);

  Manager uploadManagerFiles(Long managerId, MultipartFile idFile, MultipartFile criminalFile,
      MultipartFile healthFile)
      throws IOException;

  Manager getManagerFiles(Long managerId);

  Manager updateManagerFiles(Long managerId, MultipartFile idFile, MultipartFile criminalFile,
      MultipartFile healthFile)
      throws IOException;

  Manager getManagerById(Long id);

  // 매니저 존재 여부 검증
  void validateManagerExists(Long managerId);

  // 매니저 존재 여부 확인 (스케줄러용 boolean 반환)
  boolean existsManager(Long managerId);

  // 활동중인 매니저 ID 조회
  List<Long> findAllActiveManagerIds(ManagerStatus status);
}
