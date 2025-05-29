package com.homeaid.service;

import com.homeaid.domain.Matching;
import com.homeaid.dto.request.MatchingCustomerResponseDto;
import com.homeaid.dto.request.MatchingCustomerResponseDto.CustomerAction;
import com.homeaid.dto.request.MatchingManagerResponseDto.ManagerAction;



public interface MatchingService {

  Long createMatching(Long managerId, Long reservationId, Matching matching);

  void respondToMatchingAsManager(Long matchingId, ManagerAction action, String memo);

  void respondToMatchingAsCustomer(Long matchingId, CustomerAction action, String memo);
}
