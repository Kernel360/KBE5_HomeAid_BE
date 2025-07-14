package com.homeaid.reservation.domain;


import com.homeaid.domain.Customer;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.CustomerRepository;
import com.homeaid.reservation.domain.enumerate.ReservationStatus;
import com.homeaid.reservation.dto.request.ReservationCommand;
import com.homeaid.reservation.exception.ReservationErrorCode;
import com.homeaid.reservation.repository.ReservationRepository;
import com.homeaid.serviceoption.domain.ServiceOption;
import com.homeaid.serviceoption.repository.ServiceOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationStoreImpl implements ReservationStore {


  private final ReservationRepository reservationRepository;
  private final CustomerRepository customerRepository;
  private final ServiceOptionRepository serviceOptionRepository;

  @Override
  public Reservation save(ReservationCommand reservationCommand) {

    Reservation reservation = reservationCommand.toEntity();

    Customer customer = customerRepository.findById(reservationCommand.getUserId())
        .orElseThrow(() -> new CustomException(UserErrorCode.CUSTOMER_NOT_FOUND));

    ServiceOption serviceOption = serviceOptionRepository.findById(reservationCommand.getOptionId())
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

    reservation.addItem(serviceOption);
    reservation.setCustomer(customer);

    log.info("[예약 생성] customerId={}, serviceOptionId={}", reservationCommand.getUserId(),
        reservationCommand.getOptionId());

    return reservationRepository.save(reservation);
  }

  @Override
  public Reservation update(ReservationCommand reservationCommand) {
    Reservation originReservation = getReservationById(reservationCommand.getReservationId());

    if (!originReservation.getCustomer().getId().equals(reservationCommand.getUserId())) {
      log.warn("[예약 수정 실패] 권한 없음 - reservationId={}, userId={}", reservationCommand.getReservationId(), reservationCommand.getUserId());
      throw new CustomException(ReservationErrorCode.UNAUTHORIZED_RESERVATION_ACCESS);
    }

    if (originReservation.getStatus() != ReservationStatus.REQUESTED) {
      log.warn("[예약 수정 실패] 예약 상태 불가 - reservationId={}, status={}", reservationCommand.getReservationId(),
          originReservation.getStatus());
      throw new CustomException(ReservationErrorCode.RESERVATION_CANNOT_UPDATE);
    }

    ServiceOption serviceOption = getServiceOptionById(reservationCommand.getOptionId());

    originReservation.updateReservation(reservationCommand);

    ReservationItem item = originReservation.getItem();
    item.updateItem(serviceOption);

    return originReservation;
  }

  @Override
  public void delete(Long reservationId, Long userId) {
    Reservation reservation = getReservationById(reservationId);

    if (!reservation.getCustomer().getId().equals(userId)) {
      log.warn("[예약 삭제 실패] 권한 없음 - reservationId={}, userId={}", reservationId, userId);
      throw new CustomException(ReservationErrorCode.UNAUTHORIZED_RESERVATION_ACCESS);
    }

    reservation.softDelete();
  }

  private Reservation getReservationById(Long reservationId) {
    return reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));
  }

  private ServiceOption getServiceOptionById(Long serviceOptionId) {
    return serviceOptionRepository.findById(serviceOptionId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));
  }

}
