package com.homeaid.service;


import com.homeaid.domain.Manager;
import com.homeaid.domain.Matching;
import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.Weekday;
import com.homeaid.dto.request.MatchingCustomerResponseDto.CustomerAction;
import com.homeaid.dto.request.MatchingManagerResponseDto.ManagerAction;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.MatchingErrorCode;
import com.homeaid.exception.ReservationErrorCode;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.repository.MatchingRepository;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.util.RegionValidator;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

  private final ManagerRepository managerRepository;
  private final ReservationRepository reservationRepository;
  private final MatchingRepository matchingRepository;
  private final RegionValidator regionValidator;

  @Override
  @Transactional
  public Long createMatching(Long managerId, Long reservationId,
      Matching matching) {

    Manager manager = managerRepository.findById(managerId)
        .orElseThrow(() -> new CustomException(
            UserErrorCode.MANAGER_NOT_FOUND));

    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(
            ReservationErrorCode.RESERVATION_NOT_FOUND));

    matching.setReservationAndManagerAndMatchingRound(reservation, manager,
        calculateNextMatchingRound(reservationId));

    reservation.updateStatusMatching();
    
    return matchingRepository.save(matching).getId();
  }


  @Override
  @Transactional
  public void respondToMatchingAsManager(Long userId, Long matchingId, ManagerAction action, String memo) {
    Matching matching = matchingRepository.findById(matchingId)
        .orElseThrow(() -> new CustomException(MatchingErrorCode.MATCHING_NOT_FOUND));

    if (!matching.getManager().getId().equals(userId)) {
      throw new CustomException(MatchingErrorCode.UNAUTHORIZED_MATCHING_ACCESS);
    }

    switch (action) {
      case ACCEPT -> matching.acceptByManager();
      case REJECT -> {
        if (memo == null || memo.isBlank()) {
          throw new CustomException(MatchingErrorCode.MEMO_REQUIRED_FOR_REJECTION);
        }
        matching.rejectByManager(memo);
        matching.getReservation().failedMatching();
      }
    }

  }

  @Override
  @Transactional
  public void respondToMatchingAsCustomer(Long userId, Long reservationId,
      CustomerAction action, String memo) {
    Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

    Matching matching = matchingRepository.findById(reservation.getFinalMatchingId())
        .orElseThrow(() -> new CustomException(MatchingErrorCode.MATCHING_NOT_FOUND));

    switch (action) {
      case CONFIRM -> {
        if (!reservation.getCustomerId().equals(userId)) {
          throw new CustomException(MatchingErrorCode.UNAUTHORIZED_MATCHING_ACCESS);
        }
        matching.confirmByCustomer();
        reservation.confirmMatching(matching.getManager().getId());
      }
      case REJECT -> {
        if (memo == null || memo.isBlank()) {
          throw new CustomException(MatchingErrorCode.MEMO_REQUIRED_FOR_REJECTION);
        }
        matching.rejectByCustomer(memo);
        matching.getReservation().failedMatching();
      }
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<Manager> recommendManagers(Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

    String[] addressParts = reservation.getAddress().split(" ");
    String sido = addressParts[0].trim();
    String sigungu = addressParts[1].trim();

    if (!regionValidator.isValid(sido, sigungu)) {
      throw new CustomException(ReservationErrorCode.INVALID_RESERVATION_REGION);
    }

    Weekday reservationWeekday = Weekday.from(reservation.getRequestedDate());

    LocalTime startTime = reservation.getRequestedTime();
    Integer duration = reservation.getDuration();
    LocalTime endTime = startTime.plusHours(duration);

    String optionName = reservation.getItem().getServiceOptionName();

    // Todo: 매니저 통계 테이블 만든 후에 조회된 매니저의 리뷰 수, 별점 등도 같이 조회
    return managerRepository.findMatchingManagers(sido, sigungu, reservationWeekday.name(), startTime, endTime, optionName);
  }

  // 매니저 매칭 전체 조회
  @Override
  public Page<Matching> getMatchingListByManager(Long userId, Pageable pageable) {
    if (managerRepository.findById(userId).isEmpty()) {
      throw new CustomException(UserErrorCode.MANAGER_NOT_FOUND);
    }
    return matchingRepository.findAllByManagerId(userId, pageable);
  }

  // 매니저 매칭 단건 조회
  @Override
  public Matching getMatchingByManager(Long matchingId, Long userId) {
    if (managerRepository.findById(userId).isEmpty()) {
      throw new CustomException(UserErrorCode.MANAGER_NOT_FOUND);
    }
    return matchingRepository.findById(matchingId).get();
  }


  private int calculateNextMatchingRound(Long reservationId) {
    return matchingRepository.countByReservationId(reservationId) + 1;
  }

}
