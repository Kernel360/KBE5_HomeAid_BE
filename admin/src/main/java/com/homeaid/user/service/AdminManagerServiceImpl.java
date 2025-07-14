package com.homeaid.user.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.dto.response.ManagerDocumentListResponseDto;
import com.homeaid.dto.response.ManagerResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.user.dto.request.AdminDocumentReviewRequest;
import com.homeaid.user.dto.request.AdminManagerSearchRequestDto;
import com.homeaid.user.repository.AdminManagerRepository;
import com.homeaid.user.spec.ManagerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminManagerServiceImpl implements AdminManagerService {

  private final AdminManagerRepository adminManagerRepository;
  private final ManagerRepository managerRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<Manager> searchManagers(AdminManagerSearchRequestDto dto, Pageable pageable) {
    return adminManagerRepository.findAll(ManagerSpecification.search(dto), pageable);
  }

  @Override
  @Transactional
  public void updateStatus(Long id, ManagerStatus status) {
    Manager manager = adminManagerRepository.findById(id)
        .orElseThrow(() -> new CustomException(UserErrorCode.MANAGER_NOT_FOUND));

    manager.changeStatus(status);
  }

  @Transactional
  @Override
  public void reviewDocument(Long id, AdminDocumentReviewRequest request) {
    Manager manager = adminManagerRepository.findById(id)
        .orElseThrow(() -> new CustomException(UserErrorCode.MANAGER_NOT_FOUND));

    ManagerStatus status = request.getStatus();

    if (request.getStatus() == ManagerStatus.REJECTED) {
      manager.reject(request.getRejectionReason());  // 메모 저장
    } else {
      throw new CustomException(UserErrorCode.INVALID_REVIEW_STATUS);
    }
  }

  @Transactional(readOnly = true)
  public ManagerResponseDto getManagerDetail(Long managerId) {
    Manager manager = managerRepository.findById(managerId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    return ManagerResponseDto.toDto(manager);
  }

  @Transactional(readOnly = true)
  public ManagerDocumentListResponseDto getManagerDocuments(Long managerId) {
    Manager manager = managerRepository.findById(managerId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    return ManagerDocumentListResponseDto.toDto(manager);
  }

}