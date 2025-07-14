package com.homeaid.reservation.domain;


import com.homeaid.domain.Customer;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.CustomerRepository;
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

    return reservationRepository.save(reservation);
  }
  
}
