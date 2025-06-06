package com.homeaid.service;

import com.homeaid.dto.request.ManagerDetailInfoRequestDto;

public interface ManagerService {


  void saveManagerDetailInfo(Long managerId, ManagerDetailInfoRequestDto requestDto);
}
