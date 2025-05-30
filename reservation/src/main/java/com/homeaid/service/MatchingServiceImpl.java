package com.homeaid.service;


import com.homeaid.domain.Manager;
import com.homeaid.domain.Matching;
import com.homeaid.domain.Reservation;
import com.homeaid.dto.request.MatchingCustomerResponseDto.CustomerAction;
import com.homeaid.dto.request.MatchingManagerResponseDto.ManagerAction;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.MatchingErrorCode;
import com.homeaid.exception.ReservationErrorCode;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.MatchingRepository;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

  private final UserRepository userRepository;
  private final ReservationRepository reservationRepository;
  private final MatchingRepository matchingRepository;

  @Override
  public Long createMatching(Long managerId, Long reservationId,
      Matching matching) {

    Manager manager = (Manager) userRepository.findById(managerId).orElseThrow(() -> new CustomException(
        UserErrorCode.MANAGER_NOT_FOUND));

    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(
            ReservationErrorCode.RESERVATION_NOT_FOUND));

    matching.setReservationAndManagerAndMatchingRound(reservation, manager,
        calculateNextMatchingRound(reservationId));

    return matchingRepository.save(matching).getId();
  }


  @Override
  public void respondToMatchingAsManager(Long matchingId, ManagerAction action, String memo) {
    Matching matching = matchingRepository.findById(matchingId)
        .orElseThrow(() -> new CustomException(MatchingErrorCode.MATCHING_NOT_FOUND));

    switch (action) {
      case ACCEPT -> matching.acceptByManager();
      case REJECT -> {
        if (memo == null || memo.isBlank()) {
         throw new CustomException(MatchingErrorCode.MEMO_REQUIRED_FOR_REJECTION);
        }
        matching.rejectByManager(memo);
      }
    }

  }

  @Override
  public void respondToMatchingAsCustomer(Long matchingId,
      CustomerAction action, String memo) {
    Matching matching = matchingRepository.findById(matchingId)
        .orElseThrow(() -> new CustomException(MatchingErrorCode.MATCHING_NOT_FOUND));

    switch (action) {
      case CONFIRM -> {
        matching.confirmByCustomer();
        Reservation reservation = matching.getReservation();
        reservation.confirmMatching(matchingId);
      }
      case REJECT -> {
        if (memo == null || memo.isBlank()) {
          throw new CustomException(MatchingErrorCode.MEMO_REQUIRED_FOR_REJECTION);
        }
        matching.rejectByCustomer(memo);
      }
    }
  }

  private int calculateNextMatchingRound(Long reservationId) {
    return matchingRepository.countByReservationId(reservationId) + 1;
  }
}
