package com.homeaid.service;

import com.homeaid.domain.Manager;
import com.homeaid.domain.Matching;
import com.homeaid.domain.Reservation;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ReservationErrorCode;
import com.homeaid.repository.MatchingRepository;
import com.homeaid.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMatchingServiceImpl implements AdminMatchingService {

//  private final ManagerRepository managerRepository;
  private final ReservationRepository reservationRepository;
  private final MatchingRepository matchingRepository;

  @Override
  public Long createMatching(Long managerId, Long reservationId,
      Matching matching) {

   // Todo: 매니저 조회

    Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new CustomException(
        ReservationErrorCode.RESERVATION_NOT_FOUND));

    // Todo: 매니저 수정
    matching.setReservationAndManagerAndMatchingRound(reservation, Manager.builder().build(), calculateNextMatchingRound(reservationId));

    return matchingRepository.save(matching).getId();
  }

  private int calculateNextMatchingRound(Long reservationId) {
    return matchingRepository.countByReservationId(reservationId) + 1;
  }
}
