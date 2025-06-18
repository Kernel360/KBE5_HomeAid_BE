package com.homeaid.service;

import com.homeaid.dto.request.UserWithdrawalRequestDto;

public interface UserWithdrawalRequestService {
  void requestWithdrawal(Long userId, UserWithdrawalRequestDto dto);

}
