package com.homeaid.service;


import com.homeaid.domain.Manager;
import com.homeaid.domain.Matching;
import com.homeaid.domain.Reservation;
import com.homeaid.domain.enumerate.AlertType;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.domain.enumerate.Weekday;
import com.homeaid.dto.RequestAlert;
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
import java.util.Optional;
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
  private final NotificationPublisher notificationPublisher;

  @Override
  @Transactional
  public void createMatching(Long managerId, Long reservationId) {

    Manager manager = managerRepository.findById(managerId)
        .orElseThrow(() -> new CustomException(
            UserErrorCode.MANAGER_NOT_FOUND));

    Reservation reservation = findReservationById(reservationId);

    Matching newMatching = reservation.createMatching(manager);

    reservationRepository.save(reservation);

    RequestAlert createdAlert = RequestAlert.createAlert(AlertType.JOB_OFFER, managerId, UserRole.MANAGER, newMatching.getId(), null);
    notificationPublisher.publishNotification(createdAlert);
  }


  @Override
  @Transactional
  public void respondToMatchingAsManager(Long userId, Long matchingId, ManagerAction action,
      String memo) {
    Matching matching = getMatchingById(matchingId);

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
    AlertType alertType = memo == null ?
            AlertType.MANAGER_MATCHING_ACCEPTED
            : AlertType.MANAGER_MATCHING_REJECTED;

    if (alertType.equals(AlertType.MANAGER_MATCHING_ACCEPTED)) {
      RequestAlert createdAlert = RequestAlert.createAlert(AlertType.SUGGEST_MATCHING_TO_CUSTOMER,
              matching.getReservation().getCustomerId(),
              UserRole.CUSTOMER,
              matching.getReservation().getId(), null);
      notificationPublisher.publishNotification(createdAlert);
    }

    RequestAlert createdAdminAlert = RequestAlert.createAlert(alertType,null, UserRole.ADMIN, matching.getReservation().getId(), memo);
    notificationPublisher.publishAdminNotification(createdAdminAlert);
  }

  @Override
  @Transactional
  public void respondToMatchingAsCustomer(Long userId, Long matchingId,
      CustomerAction action, String memo) {


    Matching matching = getMatchingById(matchingId);

    Reservation reservation = matching.getReservation();

    switch (action) {
      case CONFIRM -> {
        if (!reservation.getCustomer().getId().equals(userId)) {
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
    AlertType alertType = memo == null || memo.isBlank() ?
            AlertType.CUSTOMER_MATCHING_ACCEPTED : AlertType.CUSTOMER_MATCHING_REJECTED;

    RequestAlert createdAlert = RequestAlert.createAlert(alertType,
            matching.getManager().getId(),
            UserRole.MANAGER,
            matching.getReservation().getId(), memo);

    notificationPublisher.publishNotification(createdAlert);

    RequestAlert createdAdminAlert = RequestAlert.createAlert(alertType, null,
            UserRole.ADMIN,
            matching.getReservation().getId(), memo);

    notificationPublisher.publishAdminNotification(createdAdminAlert);

  }

  @Override
  @Transactional(readOnly = true)
  public List<Manager> recommendManagers(Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

    String[] addressParts = reservation.getAddress().split(" ");
    String sido = addressParts[0].trim();
    String sigungu = addressParts[1].trim();

    String normalizedSido = regionValidator.normalizeSido(sido);

    if (!regionValidator.isValid(normalizedSido, sigungu)) {
      throw new CustomException(ReservationErrorCode.INVALID_RESERVATION_REGION);
    }

    Weekday reservationWeekday = Weekday.from(reservation.getRequestedDate());

    LocalTime startTime = reservation.getRequestedTime();
    Integer duration = reservation.getDuration();
    LocalTime endTime = startTime.plusHours(duration);
    String optionName = reservation.getItem().getServiceOptionName();

    // Todo: 매니저 통계 테이블 만든 후에 조회된 매니저의 리뷰 수, 별점 등도 같이 조회
    return managerRepository.findMatchingManagers(normalizedSido, sigungu,
        reservationWeekday.name(), startTime, endTime, optionName);
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
  public Matching getMatchingByManager(Long reservationId, Long userId) {
    if (managerRepository.findById(userId).isEmpty()) {
      throw new CustomException(UserErrorCode.MANAGER_NOT_FOUND);
    }
    return matchingRepository.findTopByReservationIdOrderByModifiedDateDesc(reservationId).get();
  }

  private Reservation findReservationById(Long reservationId) {
    return reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));
  }

  private Optional<Matching> getLatestMatching(Reservation reservation) {
    return reservation.getLatestMatching();
  }

  private Matching getMatchingById(Long matchingId) {
    return matchingRepository.findById(matchingId)
        .orElseThrow(() -> new CustomException(MatchingErrorCode.MATCHING_NOT_FOUND));
  }

}
