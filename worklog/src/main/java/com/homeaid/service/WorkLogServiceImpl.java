package com.homeaid.service;

import com.homeaid.domain.Reservation;
import com.homeaid.domain.WorkLog;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ReservationErrorCode;
import com.homeaid.exception.WorkLogErrorCode;
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
    private final static int CHECK_RANGE_DISTANCE_METER = 500; //500미터

    @Transactional
    @Override
    public WorkLog createWorkLog(WorkLog workLog, Long reservationId, Double latitude, Double longitude) {
        //요청 한 예약건이 존재하는 예약건인지 검증
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(()
                -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));
        //존재한 예약건에 대해 요청한 매니저 아이디가 체크인을 한적이있는지 검증
        if (workLogRepository.existsWorkLogByManagerIdAndReservationId(workLog.getManagerId(), reservationId)) {
            throw new CustomException(WorkLogErrorCode.ALREADY_COMPLETED_CHECKIN);
        }

        if (!isValidDistance(reservationId, latitude, longitude)) {
            throw new CustomException(WorkLogErrorCode.OUT_OF_WORK_RANGE);
        }
        workLog.assignReservation(reservation);

        return workLogRepository.save(workLog);
    }

    @Transactional
    @Override
    public WorkLog updateWorkLogForCheckOut(WorkLog requestWorkLog, Long requestWorkLogId, Double latitude, Double longitude) {
        WorkLog workLog = workLogRepository.findById(requestWorkLogId).orElseThrow(() ->
              new CustomException(WorkLogErrorCode.WORKLOG_NOT_FOUND)
        );

        isValidManager(workLog.getId(), requestWorkLog.getManagerId());

        if (!isValidDistance(workLog.getReservation().getId(), latitude, longitude)) {
            throw new CustomException(WorkLogErrorCode.OUT_OF_WORK_RANGE);
        }
        workLog.updateCheckOutTime(requestWorkLog);

        updateReservationStatusCompleted(workLog.getReservation());

        return workLog;
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
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));
        double calculatedDistance = GeoUtils.calculateDistanceInMeters(reservation.getLatitude(), reservation.getLongitude(), latitude, longitude);

        return calculatedDistance < CHECK_RANGE_DISTANCE_METER;
    }

    public void isValidManager(Long workLogId, Long requestManagerId) {
        WorkLog workLog = workLogRepository.findById(workLogId)
                .orElseThrow(() -> new CustomException(WorkLogErrorCode.WORKLOG_NOT_FOUND));

        if (!workLog.getManagerId().equals(requestManagerId)) {
            throw new CustomException(WorkLogErrorCode.CHECKOUT_MANAGER_MISMATCH);
        }
    }

    public void updateReservationStatusCompleted(Reservation reservation) {
        reservation.updateStatusCompleted();
    }


}
