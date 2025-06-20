package com.homeaid.service;

import com.homeaid.domain.User;
import com.homeaid.domain.UserWithdrawalRequest;
import com.homeaid.dto.request.UserWithdrawalRequestDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.WithdrawalErrorCode;
import com.homeaid.repository.UserRepository;
import com.homeaid.repository.UserWithdrawalRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserWithdrawalRequestServiceImpl implements UserWithdrawalRequestService {

  private final UserRepository userRepository;
  private final UserWithdrawalRequestRepository requestRepository;

  @Override
  public void requestWithdrawal(Long userId, UserWithdrawalRequestDto dto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(WithdrawalErrorCode.USER_NOT_FOUND));

    if (user.getWithdrawalRequest() != null) {
      throw new CustomException(WithdrawalErrorCode.ALREADY_REQUESTED);
    }

    UserWithdrawalRequest request = UserWithdrawalRequest.builder()
        .user(user)
        .reason(dto.getReason())
        .build();

    user.setWithdrawalRequest(request); // 양방향 연관관계 설정
    userRepository.save(user); // cascade 로 인해 request 도 저장됨
  }
}
