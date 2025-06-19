package com.homeaid.user.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.enumerate.ManagerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminManagerService {

  Page<Manager> getAllManagers(Pageable pageable);

  void updateStatus(Long id, ManagerStatus status);

}
