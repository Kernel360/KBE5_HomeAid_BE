package com.homeaid.user.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.user.dto.request.AdminManagerSearchRequestDto;
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
  public Page<Manager> searchManagers(AdminManagerSearchRequestDto dto, Pageable pageable) {
    String name = dto.getName();
    String phone = dto.getPhone();
    String career = dto.getCareer();
    ManagerStatus status = dto.getStatus();

    // 1. 이름만 있을 경우
    if (name != null && !name.isBlank()) {
      return adminManagerRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    // 2. 전화번호만 있을 경우
    if (phone != null && !phone.isBlank()) {
      return adminManagerRepository.findByPhoneContaining(phone, pageable);
    }

    // 3. 경력만 있을 경우
    if (career != null && !career.isBlank()) {
      return adminManagerRepository.findByCareerContaining(career, pageable);
    }

    // 4. 상태만 있을 경우
    if (status != null) {
      return adminManagerRepository.findByStatus(status, pageable);
    }

    // 5. 조건 없음 → 전체 조회
    return adminManagerRepository.findAll(pageable);
  }
  // 만약 이름+전화번호처럼 복합 검색을 할려면 Querydsl 사용 추천

  // TODO : 파일 업로드 확인 후 상태값 변경할 수 있도록 수정 필수
//  @Override
//  @Transactional
//  public void updateStatus(Long id, ManagerStatus status) {
//    Manager manager = adminManagerRepository.findById(id)
//        .orElseThrow(() -> new CustomException(UserErrorCode.MANAGER_NOT_FOUND));
//
//    manager.changeStatus(status);
//  }

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