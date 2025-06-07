package com.homeaid.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.dto.request.ManagerDetailInfoRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManagerService {


  void saveManagerDetailInfo(Long managerId, ManagerDetailInfoRequestDto requestDto);

  Page<Manager> getAllManagers(Pageable pageable);

  void updateStatus(Long id, ManagerStatus status);
}
