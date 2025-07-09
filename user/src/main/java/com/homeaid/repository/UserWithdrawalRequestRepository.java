package com.homeaid.repository;

import com.homeaid.domain.UserWithdrawalRequest;
import com.homeaid.worklog.domain.enumerate.RequestStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWithdrawalRequestRepository extends
    JpaRepository<UserWithdrawalRequest, Long> {
  List<UserWithdrawalRequest> findByStatus(RequestStatus status);

  @Query("SELECT u FROM UserWithdrawalRequest u JOIN FETCH u.user")
  List<UserWithdrawalRequest> findAllWithUser();

}
