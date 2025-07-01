package com.homeaid.service;


import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.domain.Matching;
import com.homeaid.domain.Reservation;
import com.homeaid.domain.ReservationItem;
import com.homeaid.domain.enumerate.NotificationEventType;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.dto.RequestAlert;
import com.homeaid.dto.response.ReservationResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.MatchingErrorCode;
import com.homeaid.exception.ReservationErrorCode;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.*;
import com.homeaid.serviceoption.domain.ServiceOption;
import com.homeaid.serviceoption.repository.ServiceOptionRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

  private final ManagerRepository managerRepository;

  private final MatchingRepository matchingRepository;

  private final ServiceOptionRepository serviceOptionRepository;
  private final SseNotificationService sseNotificationService;

  @Override
  @Transactional
  public Reservation createReservation(Reservation reservation, Long serviceOptionId) {
    log.info("[예약 생성] customerId={}, serviceOptionId={}", reservation.getCustomerId(), serviceOptionId);
    ServiceOption serviceOption = serviceOptionRepository.findById(serviceOptionId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    reservation.addItem(serviceOption);

    Reservation savedReservation = reservationRepository.save(reservation);

    RequestAlert requestAlert = RequestAlert.builder()
            .notificationEventType(NotificationEventType.RESERVATION_CREATED)
            .targetRole(UserRole.ADMIN)
            .relatedEntityId(savedReservation.getId())
            .build();
    sseNotificationService.createAdminAlertByRequestAlert(requestAlert);

    return savedReservation;
  }

  @Override
  @Transactional(readOnly = true)
  public ReservationResponseDto getReservation(Long id) {
    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

    Long finalMatchingId = reservation.getFinalMatchingId();

    Manager manager = null;
    if (reservation.getManagerId() != null) {
      manager = managerRepository.findById(reservation.getManagerId())
          .orElseThrow(() -> new CustomException(UserErrorCode.MANAGER_NOT_FOUND));
    }

    if (finalMatchingId != null) {
      Matching matching = matchingRepository.findById(finalMatchingId)
          .orElseThrow(() -> new CustomException(MatchingErrorCode.MATCHING_NOT_FOUND));
      return ReservationResponseDto.toDto(reservation, matching.getStatus(), (manager != null) ? manager.getName() : null);
    } else {
      return ReservationResponseDto.toDto(reservation, null, (manager != null) ? manager.getName() : null);
    }
  }

  @Override
  @Transactional
  public Reservation updateReservation(Long reservationId, Long userId, Reservation newReservation,
      Long serviceOptionId) {
    Reservation originReservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

    if (!originReservation.getCustomerId().equals(userId)) {
      log.warn("[예약 수정 실패] 권한 없음 - reservationId={}, userId={}", reservationId, userId);
      throw new CustomException(ReservationErrorCode.UNAUTHORIZED_RESERVATION_ACCESS);
    }

    if (originReservation.getStatus() != ReservationStatus.REQUESTED) {
      log.warn("[예약 수정 실패] 예약 상태 불가 - reservationId={}, status={}", reservationId, originReservation.getStatus());
      throw new CustomException(ReservationErrorCode.RESERVATION_CANNOT_UPDATE);
    }

    ServiceOption serviceOption = serviceOptionRepository.findById(serviceOptionId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.SERVICE_OPTION_NOT_FOUND));

    originReservation.updateReservation(newReservation, serviceOption.getPrice(),
        newReservation.getDuration());

    ReservationItem item = originReservation.getItem();
    item.updateItem(serviceOption);

    return originReservation;
  }


  @Override
  @Transactional
  public void deleteReservation(Long reservationId, Long userId) {

    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

    if (!reservation.getCustomerId().equals(userId)) {
      log.warn("[예약 삭제 실패] 권한 없음 - reservationId={}, userId={}", reservationId, userId);
      throw new CustomException(ReservationErrorCode.UNAUTHORIZED_RESERVATION_ACCESS);
    }

    reservation.softDelete();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ReservationResponseDto> getReservations(Pageable pageable, ReservationStatus status) {
    Page<Reservation> reservations = (status != null)
        ? reservationRepository.findByStatus(status, pageable)
        : reservationRepository.findAll(pageable);

    // 고객 ID 모으기
    List<Long> customerIds = reservations.stream()
        .map(Reservation::getCustomerId)
        .distinct()
        .toList();

    // 매니저 ID 모으기 (nullable 처리 필요)
    List<Long> managerIds = reservations.stream()
        .map(Reservation::getManagerId)
        .filter(Objects::nonNull)
        .distinct()
        .toList();

    // 일괄 조회
    Map<Long, Customer> customerMap = customerRepository.findByIdIn(customerIds).stream()
        .collect(Collectors.toMap(Customer::getId, Function.identity()));

    Map<Long, Manager> managerMap = managerRepository.findByIdIn(managerIds).stream()
        .collect(Collectors.toMap(Manager::getId, Function.identity()));

    return reservations.map(reservation -> {
      Customer customer = customerMap.get(reservation.getCustomerId());
      if (customer == null) {
        log.error("[예약 조회 실패] 고객 정보 없음 - reservationId={}, customerId={}", reservation.getId(), reservation.getCustomerId());
        throw new CustomException(UserErrorCode.CUSTOMER_NOT_FOUND);
      }

      Manager manager = reservation.getManagerId() != null
          ? managerMap.get(reservation.getManagerId())
          : null;

      return ReservationResponseDto.toDto(reservation, customer, manager);
    });
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Reservation> getReservationsByCustomer(Long userId, Pageable pageable) {
    return reservationRepository.findAllByCustomerId(userId, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ReservationResponseDto> getReservationsByManager(Long managerId, Pageable pageable) {
    Page<Reservation> reservations = reservationRepository.findAllByManagerId(managerId, pageable);

    Map<Long, Customer> customerMap = batchGetCustomersFromReservations(reservations);

    return reservations.map(reservation -> {
      Customer customer = customerMap.get(reservation.getCustomerId());

      if (customer == null) {
        log.error("[매니저 예약 조회 실패] 고객 정보 없음 - reservationId={}, customerId={}", reservation.getId(), reservation.getCustomerId());
        throw new CustomException(UserErrorCode.CUSTOMER_NOT_FOUND);
      }

      Optional<Matching> matching = matchingRepository.findTopByReservationIdOrderByModifiedDateDesc(reservation.getId());

      if (matching.isPresent()) {
        return ReservationResponseDto.toDtoForManager(reservation, customer, matching.get().getStatus());
      }
      return ReservationResponseDto.toDtoForManager(reservation, customer, null);
    });
  }

  private Map<Long, Customer> batchGetCustomersFromReservations(Page<Reservation> reservations) {
    List<Long> customerIds = reservations.stream()
        .map(Reservation::getCustomerId)
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
    boolean isCustomer = userId.equals(reservation.getCustomerId());

    if (!isManager && !isCustomer) {
      throw new CustomException(ReservationErrorCode.USER_ACCESS_DENIED);
    }
  }
}
