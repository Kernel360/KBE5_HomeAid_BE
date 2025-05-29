package com.homeaid.service;



import com.homeaid.domain.Reservation;
import com.homeaid.domain.ReservationItem;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.serviceoption.domain.ServiceSubOption;
import com.homeaid.serviceoption.repository.ServiceSubOptionRepository;
import lombok.RequiredArgsConstructor;
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
        .orElseThrow(() -> new RuntimeException("서비스 옵션을 찾을 수 없습니다."));
    reservation.addItem(serviceSubOption);
    return reservationRepository.save(reservation);
  }

  @Override
  @Transactional(readOnly = true)
  public Reservation getReservation(Long id) {
    return reservationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));
  }

  @Override
  public Reservation updateReservation(Long id, Reservation newReservation,
      Long serviceSubOptionId) {
    Reservation originReservation = reservationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));

    if (originReservation.getStatus() != ReservationStatus.REQUESTED) {
      throw new RuntimeException("예약은 요청 상태(REQUESTED)일 때만 수정할 수 있습니다.");
    }

    ServiceSubOption serviceSubOption = serviceSubOptionRepository.findById(serviceSubOptionId)
        .orElseThrow(() -> new RuntimeException("서비스 옵션을 찾을 수 없습니다."));

    originReservation.updateReservation(newReservation, serviceSubOption.getBasePrice(),
        serviceSubOption.getDurationMinutes());

    ReservationItem item = originReservation.getItem();
    item.updateItem(serviceSubOption);

    return originReservation;
  }


  @Override
  public void deleteReservation(Long id) {
    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));

    reservation.softDelete();
  }
}
