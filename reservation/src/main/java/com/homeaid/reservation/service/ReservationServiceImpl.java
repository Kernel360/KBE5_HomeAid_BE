package com.homeaid.reservation.service;


import com.homeaid.domain.Customer;
import com.homeaid.matching.domain.Matching;
import com.homeaid.reservation.domain.Reservation;
import com.homeaid.reservation.domain.ReservationItem;
import com.homeaid.matching.controller.enumerate.MatchingStatus;
import com.homeaid.domain.enumerate.AlertType;
import com.homeaid.reservation.domain.enumerate.ReservationStatus;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.RequestAlert;
import com.homeaid.reservation.dto.response.ManagerReservationResponseDto;
import com.homeaid.reservation.dto.response.ReservationResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.reservation.exception.ReservationErrorCode;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.*;
import com.homeaid.reservation.repository.ReservationRepository;
import com.homeaid.service.NotificationPublisher;
import com.homeaid.serviceoption.domain.ServiceOption;
import com.homeaid.serviceoption.repository.ServiceOptionRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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

  private final ReservationRepository reservationRepository;

  private final CustomerRepository customerRepository;


  private final ServiceOptionRepository serviceOptionRepository;

  private final NotificationPublisher notificationPublisher;

  @Override
  @Transactional
  public Reservation createReservation(Reservation reservation, Long userId, Long serviceOptionId) {
    log.info("[예약 생성] customerId={}, serviceOptionId={}", userId, serviceOptionId);

    Customer customer = customerRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.CUSTOMER_NOT_FOUND));

    ServiceOption serviceOption = getServiceOptionById(serviceOptionId);

    reservation.addItem(serviceOption);
    reservation.setCustomer(customer);

    Reservation savedReservation = reservationRepository.save(reservation);

    RequestAlert createdAdminAlert = RequestAlert.createAlert(AlertType.RESERVATION_CREATED, null,
            UserRole.ADMIN,
            savedReservation.getId(), null);
    notificationPublisher.publishAdminNotification(createdAdminAlert);

    return savedReservation;
  }

  @Override
  @Transactional(readOnly = true)
  public ReservationResponseDto getReservation(Long id) {
    Reservation reservation = getReservationById(id);

    Matching latestMatching = getLatestMatching(reservation).orElse(null);

    String managerName = null;
    MatchingStatus status = null;
    Long matchingId = null;
    if (latestMatching != null) {
      managerName = latestMatching.getManager().getName();
      status = latestMatching.getStatus();
      matchingId = latestMatching.getId();
    }

    return ReservationResponseDto.toDto(reservation, status, managerName, matchingId);
  }

  @Override
  @Transactional
  public Reservation updateReservation(Long reservationId, Long userId, Reservation newReservation,
      Long serviceOptionId) {
    Reservation originReservation = getReservationById(reservationId);

    if (!originReservation.getCustomer().getId().equals(userId)) {
      log.warn("[예약 수정 실패] 권한 없음 - reservationId={}, userId={}", reservationId, userId);
      throw new CustomException(ReservationErrorCode.UNAUTHORIZED_RESERVATION_ACCESS);
    }

    if (originReservation.getStatus() != ReservationStatus.REQUESTED) {
      log.warn("[예약 수정 실패] 예약 상태 불가 - reservationId={}, status={}", reservationId,
          originReservation.getStatus());
      throw new CustomException(ReservationErrorCode.RESERVATION_CANNOT_UPDATE);
    }

    ServiceOption serviceOption = getServiceOptionById(serviceOptionId);

    originReservation.updateReservation(newReservation, serviceOption.getPrice(),
        newReservation.getDuration());

    ReservationItem item = originReservation.getItem();
    item.updateItem(serviceOption);

    return originReservation;
  }


  @Override
  @Transactional
  public void deleteReservation(Long reservationId, Long userId) {

    Reservation reservation = getReservationById(reservationId);

    if (!reservation.getCustomer().getId().equals(userId)) {
      log.warn("[예약 삭제 실패] 권한 없음 - reservationId={}, userId={}", reservationId, userId);
      throw new CustomException(ReservationErrorCode.UNAUTHORIZED_RESERVATION_ACCESS);
    }

    reservation.softDelete();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ReservationResponseDto> getReservations(Pageable pageable, ReservationStatus status) {

    Page<Reservation> reservations = reservationRepository.findByOptionalStatus(status, pageable);

    return reservations.map(reservation -> {

      Matching latestMatching = getLatestMatching(reservation).orElse(null);
      String managerName = null;
      if (latestMatching != null) {
        managerName = latestMatching.getManager().getName();
      }

      return ReservationResponseDto.toDto(reservation, reservation.getCustomer().getName(),
          managerName);
    });
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Reservation> getReservationsByCustomer(Long userId, Pageable pageable) {
    return reservationRepository.findAllByCustomerId(userId, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ManagerReservationResponseDto> getReservationsByManager(Long managerId,
      Pageable pageable) {
    Page<Reservation> reservations = reservationRepository.findAllByManagerId(managerId, pageable);

    Map<Long, Customer> customerMap = batchGetCustomersFromReservations(reservations);

    return reservations.map(reservation -> {
      Customer customer = customerMap.get(reservation.getCustomer().getId());

      if (customer == null) {
        log.error("[매니저 예약 조회 실패] 고객 정보 없음 - reservationId={}, customerId={}", reservation.getId(),
            reservation.getCustomer().getId());
        throw new CustomException(UserErrorCode.CUSTOMER_NOT_FOUND);
      }

      Matching matching = getLatestMatching(reservation).get();

      return ManagerReservationResponseDto.toDto(reservation, customer, matching);
    });
  }

  private Map<Long, Customer> batchGetCustomersFromReservations(Page<Reservation> reservations) {
    List<Long> customerIds = reservations.stream()
        .map(reservation -> reservation.getCustomer().getId())
        .distinct()
        .toList();

    return customerRepository.findByIdIn(customerIds).stream()
        .collect(Collectors.toMap(Customer::getId, Function.identity()));

  }

  @Override
  public Reservation validateReservation(Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

    if (reservation.getStatus() != ReservationStatus.COMPLETED) {
      throw new CustomException(ReservationErrorCode.RESERVATION_NOT_COMPLETED);
    }

    return reservation;
  }

  @Override
  public void validateManagerAccess(Reservation reservation, Long managerId) {
    if (!reservation.getManagerId().equals(managerId)) {
      throw new CustomException(ReservationErrorCode.RESERVATION_MANAGER_MISMATCH);
    }
  }

  @Override
  public void validateUserAccess(Reservation reservation, Long userId) {
    boolean isManager = userId.equals(reservation.getManagerId());
    boolean isCustomer = userId.equals(reservation.getCustomer().getId());

    if (!isManager && !isCustomer) {
      throw new CustomException(ReservationErrorCode.USER_ACCESS_DENIED);
    }
  }

  private ServiceOption getServiceOptionById(Long serviceOptionId) {
    return serviceOptionRepository.findById(serviceOptionId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));
  }

  private Reservation getReservationById(Long reservationId) {
    return reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));
  }

  private Optional<Matching> getLatestMatching(Reservation reservation) {
    return reservation.getLatestMatching();
  }

}
