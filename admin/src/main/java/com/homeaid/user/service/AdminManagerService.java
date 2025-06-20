package com.homeaid.user.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import com.homeaid.user.dto.request.AdminManagerSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminManagerService {

  Page<Manager> searchManagers(AdminManagerSearchRequestDto dto, Pageable pageable);

  //void updateStatus(Long id, ManagerStatus status);

}
