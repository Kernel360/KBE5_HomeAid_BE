package com.homeaid.matching.service;

import com.homeaid.domain.Manager;
import com.homeaid.matching.domain.Matching;
import com.homeaid.matching.dto.request.MatchingCustomerResponseDto.CustomerAction;
import com.homeaid.matching.dto.request.MatchingManagerResponseDto.ManagerAction;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MatchingService {

  void createMatching(Long managerId, Long reservationId);

  void respondToMatchingAsManager(Long userId, Long matchingId, ManagerAction action, String memo);

  void respondToMatchingAsCustomer(Long userId, Long matchingId, CustomerAction action, String memo);

  List<Manager> recommendManagers(Long reservationId);

  Page<Matching> getMatchingListByManager(Long userId, Pageable pageable);

  Matching getMatchingByManager(Long matchingId, Long userId);
}
