package com.homeaid.service;

import com.homeaid.domain.Reservation;
import com.homeaid.domain.WorkLog;
import com.homeaid.domain.enumerate.NotificationEventType;
import com.homeaid.domain.enumerate.WorkType;
import com.homeaid.dto.RequestAlert;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ReservationErrorCode;
import com.homeaid.exception.WorkLogErrorCode;
import com.homeaid.repository.MatchingRepository;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.repository.WorkLogRepository;
import com.homeaid.util.GeoUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class WorkLogServiceImpl implements WorkLogService {

  private final WorkLogRepository workLogRepository;
  private final ReservationRepository reservationRepository;
  private final static int CHECK_RANGE_DISTANCE_METER = 1000; //500미터
  private final MatchingRepository matchingRepository;
  private final NotificationPublisher notificationPublisher;

  @Transactional
  @Override
  public WorkLog createWorkLog(Long userId, Long reservationId, Double latitude, Double longitude) {
    //요청 한 예약건이 존재하는 예약건인지 검증
    Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(()
        -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    //존재한 예약건에 대해 요청한 매니저 아이디가 체크인을 한적이있는지 검증
    if (workLogRepository.existsWorkLogByManagerIdAndReservationId(userId, reservationId)) {
      throw new CustomException(WorkLogErrorCode.ALREADY_COMPLETED_CHECKIN);
    }

    if (!isValidDistance(reservationId, latitude, longitude)) {
      throw new CustomException(WorkLogErrorCode.OUT_OF_WORK_RANGE);
    }
    WorkLog workLog = WorkLog.builder().workType(WorkType.CHECKIN).managerId(userId)
        .reservation(reservation).build();

    RequestAlert requestAlert = RequestAlert.builder()
            .targetId(reservation.getCustomerId())
            .notificationEventType(NotificationEventType.WORK_CHECKIN)
            .relatedEntityId(reservationId)
            .build();
    notificationPublisher.publishNotification(requestAlert);

    return workLogRepository.save(workLog);
  }

  @Transactional
  @Override
  public void updateWorkLogForCheckOut(Long userId, Long reservationId,
      Double latitude, Double longitude) {

    WorkLog workLog = isValidManager(reservationId, userId);

    if (!isValidDistance(workLog.getReservation().getId(), latitude, longitude)) {
      throw new CustomException(WorkLogErrorCode.OUT_OF_WORK_RANGE);
    }

    workLog.updateCheckOut();

    updateReservationStatusCompleted(workLog.getReservation());

    RequestAlert requestAlert = RequestAlert.builder()
            .targetId(workLog.getReservation().getCustomerId())
            .notificationEventType(NotificationEventType.WORK_CHECKOUT)
            .relatedEntityId(reservationId)
            .build();
    notificationPublisher.publishNotification(requestAlert);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<WorkLog> getAllWorkLogsByManager(Long userId, Pageable pageable) {
    return workLogRepository.findAllByManagerId(userId, pageable);
  }

  /**
   * @param reservationId 예약 위치 조회할 id
   * @return 예약 위치와 체크인 위치의 차이가 CHECK_RANGE_DISTANCE_METER 범위 안에 있으면 true
   */
  private boolean isValidDistance(Long reservationId, Double latitude, Double longitude) {
    System.out.println(latitude + "+TTHhH+H+h+===" + longitude);
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    double calculatedDistance = GeoUtils.calculateDistanceInMeters(reservation.getLatitude(),
        reservation.getLongitude(), latitude, longitude);

    return calculatedDistance < CHECK_RANGE_DISTANCE_METER;
  }

  public WorkLog isValidManager(Long reservationId, Long requestManagerId) {
    WorkLog workLog = workLogRepository.findByReservationId(reservationId).orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

    if (!workLog.getManagerId().equals(requestManagerId)) {
      throw new CustomException(WorkLogErrorCode.CHECKOUT_MANAGER_MISMATCH);
    }

    return workLog;
  }

  public void updateReservationStatusCompleted(Reservation reservation) {
    reservation.updateStatusCompleted();
  }


}
