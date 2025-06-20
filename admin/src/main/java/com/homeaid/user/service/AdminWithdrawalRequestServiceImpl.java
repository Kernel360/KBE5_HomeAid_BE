package com.homeaid.user.service;

import com.homeaid.domain.UserWithdrawalRequest;
import com.homeaid.domain.enumerate.RequestStatus;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.WithdrawalErrorCode;
import com.homeaid.user.repository.AdminWithdrawalRequestRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminWithdrawalRequestServiceImpl implements AdminWithdrawalRequestService {

  private final AdminWithdrawalRequestRepository withdrawalRequestRepository;

  @Override
  public List<UserWithdrawalRequest> getPendingRequests() {
    return withdrawalRequestRepository.findByStatus(RequestStatus.PENDING);
  }

  @Override
  @Transactional
  public void processWithdrawal(Long requestId, boolean approve) {
    UserWithdrawalRequest request = withdrawalRequestRepository.findById(requestId)
        .orElseThrow(() -> new CustomException(WithdrawalErrorCode.REQUEST_NOT_FOUND));

    if (approve) {
      request.approve();
      request.getUser().delete(); // 유저에 soft delete 로직 구현되어 있어야 함
    } else {
      request.reject();
    }
  }
}