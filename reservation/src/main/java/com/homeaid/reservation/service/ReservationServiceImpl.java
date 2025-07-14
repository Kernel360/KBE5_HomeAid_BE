package com.homeaid.reservation.service;


import com.homeaid.domain.Customer;
import com.homeaid.matching.domain.Matching;
import com.homeaid.reservation.domain.Reservation;
import com.homeaid.matching.controller.enumerate.MatchingStatus;
import com.homeaid.domain.enumerate.AlertType;
import com.homeaid.reservation.domain.ReservationReader;
import com.homeaid.reservation.domain.ReservationStore;
import com.homeaid.reservation.domain.enumerate.ReservationStatus;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.RequestAlert;
import com.homeaid.reservation.dto.ReservationDtoMapper;
import com.homeaid.reservation.dto.request.ReservationCommand;
import com.homeaid.reservation.dto.response.ManagerReservationResponseDto;
import com.homeaid.reservation.dto.response.ReservationInfo;
import com.homeaid.reservation.dto.response.ReservationResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.reservation.exception.ReservationErrorCode;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.service.NotificationPublisher;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

  private final ReservationStore reservationStore;

  private final ReservationReader reservationReader;

  private final ReservationDtoMapper reservationDtoMapper;

  private final NotificationPublisher notificationPublisher;

  @Override
  @Transactional
  public ReservationInfo createReservation(ReservationCommand reservationCommand) {
    Reservation savedReservation = reservationStore.save(reservationCommand);

    RequestAlert createdAdminAlert = RequestAlert.createAlert(AlertType.RESERVATION_CREATED, null,
        UserRole.ADMIN,
        savedReservation.getId(), null);
    notificationPublisher.publishAdminNotification(createdAdminAlert);

    return ReservationInfo.toInfo(savedReservation);
  }

  @Override
  @Transactional(readOnly = true)
  public ReservationInfo getReservation(Long reservationId) {
    Reservation reservation = reservationReader.getReservation(reservationId);

    Matching latestMatching = getLatestMatching(reservation).orElse(null);

    String managerName = null;
    MatchingStatus status = null;
    Long matchingId = null;

    if (latestMatching != null) {
      managerName = latestMatching.getManager().getName();
      status = latestMatching.getStatus();
      matchingId = latestMatching.getId();
    }

    return ReservationInfo.toInfo(reservation, status, managerName, matchingId);
  }

  @Override
  @Transactional
  public ReservationInfo updateReservation(ReservationCommand reservationCommand) {
    return ReservationInfo.toInfo(reservationStore.update(reservationCommand));
  }


  @Override
  @Transactional
  public void deleteReservation(Long reservationId, Long userId) {
    reservationStore.delete(reservationId, userId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ReservationResponseDto> getReservations(Pageable pageable, ReservationStatus status) {

    Page<Reservation> reservations = reservationReader.getReservationByStatus(status, pageable);

    return reservations.map(reservation -> {

      Matching latestMatching = getLatestMatching(reservation).orElse(null);
      String managerName = null;
      if (latestMatching != null) {
        managerName = latestMatching.getManager().getName();
      }

      return reservationDtoMapper.toDto(reservation, reservation.getCustomer().getName(),
          managerName);
    });
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Reservation> getReservationsByCustomer(Long userId, Pageable pageable) {
    return reservationReader.getReservationsByCustomerId(userId, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ManagerReservationResponseDto> getReservationsByManager(Long managerId,
      Pageable pageable) {

    Page<Reservation> reservations = reservationReader.getReservationsByManagerId(managerId,
        pageable);

    Map<Long, Customer> customerMap = getCustomersFromReservations(reservations);

    return reservations.map(reservation -> {

      Customer customer = customerMap.get(reservation.getId());

      if (customer == null) {
        log.error("[매니저 예약 조회 실패] 고객 정보 없음 - reservationId={}, customerId={}", reservation.getId(),
            reservation.getCustomer().getId());
        throw new CustomException(UserErrorCode.CUSTOMER_NOT_FOUND);
      }

      Matching matching = getLatestMatching(reservation).get();

      return reservationDtoMapper.toDto(reservation, customer.getName(), matching);
    });
  }

  private Map<Long, Customer> getCustomersFromReservations(Page<Reservation> reservations) {
    return reservations.stream().collect(Collectors.toMap(Reservation::getId,
        Reservation::getCustomer));
  }

  @Override
  @Transactional(readOnly = true)
  public Reservation validateReservation(Long reservationId, Long managerId) {
    Reservation reservation = reservationReader.getReservation(reservationId);

    if (reservation.getStatus() != ReservationStatus.COMPLETED) {
      throw new CustomException(ReservationErrorCode.RESERVATION_NOT_COMPLETED);
    }

    if (!reservation.getManagerId().equals(managerId)) {
      throw new CustomException(ReservationErrorCode.RESERVATION_MANAGER_MISMATCH);
    }

    return reservation;
  }

  @Override
  @Transactional(readOnly = true)
  public void validateReservationAndUserAccess(Long reservationId, Long userId) {
    Reservation reservation = reservationReader.getReservation(reservationId);

    boolean isManager = userId.equals(reservation.getManagerId());
    boolean isCustomer = userId.equals(reservation.getCustomer().getId());

    if (!isManager && !isCustomer) {
      throw new CustomException(ReservationErrorCode.USER_ACCESS_DENIED);
    }
  }

  private Optional<Matching> getLatestMatching(Reservation reservation) {
    return reservation.getLatestMatching();
  }

}
