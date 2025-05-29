package com.homeaid.service;

import com.homeaid.domain.Reservation;
import com.homeaid.domain.WorkLog;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.WorkLogErrorCode;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.repository.WorkLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WorkLogServiceImpl implements WorkLogService {
    private final WorkLogRepository workLogRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    @Override
    public WorkLog createWorkLog(WorkLog workLog, Long reservationId, Point point) {
        if (!validateWorkLog(reservationId, point)) {
            throw new CustomException(WorkLogErrorCode.OUT_OF_RANGE);
        }
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(()
                -> new CustomException(WorkLogErrorCode.RESERVATION_NOT_FOUND));

        // 기존 작업기록이 있으면 삭제 , 이 방식이 합리적인가?
        workLogRepository.findByReservationId(reservationId)
                .ifPresent(existWorkLog -> {
                    workLogRepository.delete(existWorkLog);
                    workLogRepository.flush();
                });
        workLog.assignReservation(reservation);

        return workLogRepository.save(workLog);
    }

    private boolean validateWorkLog(Long reservationId, Point point) {
        boolean result = true;
        // Todo : 임시
        //예약id로 예약의 위도 경도 값과
        //요청 체크인 체크아웃의 위도 경도 값을 검증하는 로직
        return result;
    }


}
