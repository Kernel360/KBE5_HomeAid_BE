package com.homeaid.user.repository;

import com.homeaid.domain.UserWithdrawalRequest;
import com.homeaid.domain.enumerate.RequestStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminWithdrawalRequestRepository extends JpaRepository<UserWithdrawalRequest, Long> {
  List<UserWithdrawalRequest> findByStatus(RequestStatus status);
}
