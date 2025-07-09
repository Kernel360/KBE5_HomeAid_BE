package com.homeaid.worklog.service;


import com.homeaid.worklog.domain.WorkLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkLogService {

  WorkLog createWorkLog(Long userId, Long reservationId, Double longitude, Double latitude);

  void updateWorkLogForCheckOut(Long userId, Long reservationId,
      Double longitude, Double latitude);

  Page<WorkLog> getAllWorkLogsByManager(Long userId, Pageable pageable);
}
