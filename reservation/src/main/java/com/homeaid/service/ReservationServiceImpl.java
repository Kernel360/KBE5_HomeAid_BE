package com.homeaid.service;


import com.homeaid.domain.Reservation;
import com.homeaid.domain.ReservationItem;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ReservationErrorCode;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.serviceoption.domain.ServiceSubOption;
import com.homeaid.serviceoption.repository.ServiceSubOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

  private final ReservationRepository reservationRepository;

  private final ServiceSubOptionRepository serviceSubOptionRepository;

  @Override
  @Transactional
  public Reservation createReservation(Reservation reservation, Long serviceSubOptionId) {
    ServiceSubOption serviceSubOption = serviceSubOptionRepository.findById(serviceSubOptionId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.SERVICE_OPTION_NOT_FOUND));
    reservation.addItem(serviceSubOption);
    return reservationRepository.save(reservation);
  }

  @Override
  @Transactional(readOnly = true)
  public Reservation getReservation(Long id) {
    return reservationRepository.findById(id)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));
  }

  @Override
  @Transactional
  public Reservation updateReservation(Long reservationId, Long userId, Reservation newReservation,
      Long serviceSubOptionId) {
    Reservation originReservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

    if (!originReservation.getCustomerId().equals(userId)) {
      throw new CustomException(ReservationErrorCode.UNAUTHORIZED_RESERVATION_ACCESS)  ;
    }

    if (originReservation.getStatus() != ReservationStatus.REQUESTED) {
      throw new CustomException(ReservationErrorCode.RESERVATION_CANNOT_UPDATE);
    }

    ServiceSubOption serviceSubOption = serviceSubOptionRepository.findById(serviceSubOptionId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.SERVICE_OPTION_NOT_FOUND));

    originReservation.updateReservation(newReservation, serviceSubOption.getBasePrice(),
        serviceSubOption.getDurationMinutes());

    ReservationItem item = originReservation.getItem();
    item.updateItem(serviceSubOption);

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
  public Page<Reservation> getReservations(Pageable pageable) {
    return reservationRepository.findAll(pageable);
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
