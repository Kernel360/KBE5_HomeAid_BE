package com.homeaid.user.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.user.repository.AdminManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminManagerServiceImpl implements AdminManagerService {

  private final AdminManagerRepository adminManagerRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<Manager> getAllManagers(Pageable pageable) {
    return adminManagerRepository.findAll(pageable);

  }

  // TODO : 파일 업로드 확인 후 상태값 변경할 수 있도록 수정 필수
  @Override
  @Transactional
  public void updateStatus(Long id, ManagerStatus status) {
    Manager manager = adminManagerRepository.findById(id)
        .orElseThrow(() -> new CustomException(UserErrorCode.MANAGER_NOT_FOUND));

    manager.changeStatus(status);
  }

//  @Override
//  @Transactional
//  public void reviewDocument(Long id, DocumentReviewRequest request) {
//    Manager manager = adminManagerRepository.findById(id)
//        .orElseThrow(() -> new CustomException(UserErrorCode.MANAGER_NOT_FOUND));
//
//    if (request.getStatus() == ManagerStatus.APPROVED) {
//      manager.changeStatus(ManagerStatus.APPROVED);
//    } else if (request.getStatus() == ManagerStatus.REJECTED) {
//      manager.reject(request.getRejectionReason());
//    } else {
//      throw new CustomException(UserErrorCode.INVALID_REVIEW_STATUS);
//    }
//  }
}
