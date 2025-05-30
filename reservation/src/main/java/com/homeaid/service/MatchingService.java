package com.homeaid.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.Matching;
import com.homeaid.domain.User;
import com.homeaid.dto.request.MatchingCustomerResponseDto.CustomerAction;
import com.homeaid.dto.request.MatchingManagerResponseDto.ManagerAction;
import java.util.List;


public interface MatchingService {

  Long createMatching(Long managerId, Long reservationId, Matching matching);

  void respondToMatchingAsManager(Long matchingId, ManagerAction action, String memo);

  void respondToMatchingAsCustomer(Long matchingId, CustomerAction action, String memo);

  List<Manager> recommendManagers(Long reservationId);
}
