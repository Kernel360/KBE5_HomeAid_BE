package com.homeaid.user.service;

import com.homeaid.domain.UserWithdrawalRequest;
import java.util.List;

public interface AdminWithdrawalRequestService {

  List<UserWithdrawalRequest> getPendingRequests();
  void processWithdrawal(Long requestId, boolean approve);

}
