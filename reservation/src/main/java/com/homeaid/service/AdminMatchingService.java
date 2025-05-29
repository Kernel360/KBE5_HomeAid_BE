package com.homeaid.service;

import com.homeaid.domain.Matching;
import com.homeaid.dto.request.MatchingManagerResponseDto.Action;
import jakarta.validation.constraints.NotNull;


public interface AdminMatchingService {

  Long createMatching(Long managerId, Long reservationId, Matching matching);

  void respondToMatching(Long matchingId, Action action, String memo);

  
}
