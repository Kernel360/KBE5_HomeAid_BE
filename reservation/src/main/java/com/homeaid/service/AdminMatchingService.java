package com.homeaid.service;

import com.homeaid.domain.Matching;
import com.homeaid.dto.request.CreateMatchingRequestDto;


public interface AdminMatchingService {

  Long createMatching(Long managerId, Long reservationId, Matching matching);

}
