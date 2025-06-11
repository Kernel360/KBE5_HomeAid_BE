package com.homeaid.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.Matching;
import com.homeaid.dto.request.MatchingCustomerResponseDto.CustomerAction;
import com.homeaid.dto.request.MatchingManagerResponseDto.ManagerAction;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MatchingService {

  Long createMatching(Long managerId, Long reservationId, Matching matching);

  void respondToMatchingAsManager(Long userId, Long matchingId, ManagerAction action, String memo);

  void respondToMatchingAsCustomer(Long userId, Long matchingId, CustomerAction action, String memo);

  List<Manager> recommendManagers(Long reservationId);

  Page<Matching> getMatchingListByManager(Long userId, Pageable pageable);

  Matching getMatchingByManager(Long matchingId, Long userId);
}
