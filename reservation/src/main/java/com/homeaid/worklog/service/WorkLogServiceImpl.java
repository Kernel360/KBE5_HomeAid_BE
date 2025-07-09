package com.homeaid.worklog.service;

import com.homeaid.matching.domain.Matching;
import com.homeaid.reservation.domain.Reservation;
import com.homeaid.worklog.domain.WorkLog;
import com.homeaid.domain.enumerate.AlertType;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.worklog.domain.enumerate.WorkType;
import com.homeaid.dto.RequestAlert;
import com.homeaid.exception.CustomException;
import com.homeaid.matching.exception.MatchingErrorCode;
import com.homeaid.reservation.exception.ReservationErrorCode;
import com.homeaid.matching.repository.MatchingRepository;
import com.homeaid.reservation.repository.ReservationRepository;
import com.homeaid.worklog.exception.WorkLogErrorCode;
import com.homeaid.worklog.repository.WorkLogRepository;
import com.homeaid.worklog.util.GeoUtils;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class WorkLogServiceImpl implements WorkLogService {

  private final WorkLogRepository workLogRepository;

  private final static int CHECK_RANGE_DISTANCE_METER = 1000;

  private final MatchingRepository matchingRepository;

  private final NotificationPublisher notificationPublisher;

  @Transactional
  @Override
  public WorkLog updateWorkLogForCheckIn(Long userId, Long matchingId, Double latitude,
      Double longitude) {

    Matching matching = matchingRepository.findById(matchingId)
        .orElseThrow(() -> new CustomException(MatchingErrorCode.MATCHING_NOT_FOUND));

    WorkLog workLog = matching.getWorkLog();

    if (workLog.getCheckInTime() == null) {
      throw new CustomException(WorkLogErrorCode.ALREADY_COMPLETED_CHECKIN);
    }

    if (!isValidDistance(matching.getReservation(), latitude, longitude)) {
      throw new CustomException(WorkLogErrorCode.OUT_OF_WORK_RANGE);
    }

    LocalDateTime checkInTime = workLog.updateCheckIn();

    log.info("[WorkLog] 매니저 ID: {}, 매칭 ID: {}, 체크인 시간: {}", userId, matchingId, checkInTime);

    RequestAlert createdAlert = RequestAlert.createAlert(AlertType.WORK_CHECKIN,
            reservation.getCustomerId(),
            UserRole.CUSTOMER,
            reservationId, null);
    notificationPublisher.publishNotification(createdAlert);

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

    RequestAlert createdAlert = RequestAlert.createAlert(AlertType.WORK_CHECKOUT,
            workLog.getReservation().getCustomerId(),
            UserRole.CUSTOMER,
            reservationId,
            null);
    notificationPublisher.publishNotification(createdAlert);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<WorkLog> getAllWorkLogsByManager(Long userId, Pageable pageable) {
    return workLogRepository.findAllByManagerId(userId, pageable);
  }

  /**
   * @param reservation 예약 위치 조회할 id
   * @return 예약 위치와 체크인 위치의 차이가 CHECK_RANGE_DISTANCE_METER 범위 안에 있으면 true
   */
  private boolean isValidDistance(Reservation reservation, Double latitude, Double longitude) {
    double calculatedDistance = GeoUtils.calculateDistanceInMeters(reservation.getLatitude(),
        reservation.getLongitude(), latitude, longitude);

    return calculatedDistance < CHECK_RANGE_DISTANCE_METER;
  }

  public WorkLog isValidManager(Long reservationId, Long requestManagerId) {
    WorkLog workLog = workLogRepository.findByReservationId(reservationId)
        .orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

    if (!workLog.getManagerId().equals(requestManagerId)) {
      throw new CustomException(WorkLogErrorCode.CHECKOUT_MANAGER_MISMATCH);
    }

    return workLog;
  }

  public void updateReservationStatusCompleted(Reservation reservation) {
    reservation.updateStatusCompleted();
  }


}
