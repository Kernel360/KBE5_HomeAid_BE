package com.homeaid.user.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.dto.response.ManagerDocumentListResponseDto;
import com.homeaid.dto.response.ManagerResponseDto;
import com.homeaid.user.dto.request.AdminDocumentReviewRequest;
import com.homeaid.user.dto.request.AdminManagerSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface AdminManagerService {

  Page<Manager> searchManagers(AdminManagerSearchRequestDto dto, Pageable pageable);

  void updateStatus(Long id, ManagerStatus status);

  @Transactional
  void reviewDocument(Long id, AdminDocumentReviewRequest request);

  ManagerResponseDto getManagerDetail(Long managerId);

  ManagerDocumentListResponseDto getManagerDocuments(Long managerId);
}
