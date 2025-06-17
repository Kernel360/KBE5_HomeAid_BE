package com.homeaid.service;


import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.domain.Matching;
import com.homeaid.domain.Reservation;
import com.homeaid.domain.ReservationItem;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.dto.response.ReservationResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.MatchingErrorCode;
import com.homeaid.exception.ReservationErrorCode;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.CustomerRepository;
import com.homeaid.repository.ManagerRepository;
import com.homeaid.repository.MatchingRepository;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.serviceoption.domain.ServiceOption;
import com.homeaid.serviceoption.repository.ServiceOptionRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

  private final ReservationRepository reservationRepository;

  private final CustomerRepository customerRepository;

  private final ManagerRepository managerRepository;

  private final MatchingRepository matchingRepository;

  private final ServiceOptionRepository serviceOptionRepository;

  @Override
  @Transactional
  public Reservation createReservation(Reservation reservation, Long serviceOptionId) {
    ServiceOption serviceOption = serviceOptionRepository.findById(serviceOptionId).orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    reservation.addItem(serviceOption);
    return reservationRepository.save(reservation);
  }

  @Override
  @Transactional(readOnly = true)
  public ReservationResponseDto getReservation(Long id) {
    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

    Long finalMatchingId = reservation.getFinalMatchingId();

    if (finalMatchingId != null) {
      Matching matching = matchingRepository.findById(finalMatchingId)
          .orElseThrow(() -> new CustomException(MatchingErrorCode.MATCHING_NOT_FOUND));
      return ReservationResponseDto.toDto(reservation, matching.getStatus());
    } else {
      return ReservationResponseDto.toDto(reservation, null);
    }
  }

  @Override
  @Transactional
  public Reservation updateReservation(Long reservationId, Long userId, Reservation newReservation,
      Long serviceOptionId) {
    Reservation originReservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

    if (!originReservation.getCustomerId().equals(userId)) {
      throw new CustomException(ReservationErrorCode.UNAUTHORIZED_RESERVATION_ACCESS)  ;
    }

    if (originReservation.getStatus() != ReservationStatus.REQUESTED) {
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
      throw new CustomException(ReservationErrorCode.UNAUTHORIZED_RESERVATION_ACCESS)  ;
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
  public Page<Reservation> getReservationsByManager(Long managerId, Pageable pageable) {
    return reservationRepository.findAllByManagerId(managerId, pageable);
  }
}
