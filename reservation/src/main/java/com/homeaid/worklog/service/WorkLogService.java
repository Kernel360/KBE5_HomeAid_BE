package com.homeaid.worklog.service;


import com.homeaid.worklog.domain.WorkLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkLogService {

  void updateWorkLogForCheckIn(Long userId, Long matchingId, Double longitude, Double latitude);

  void updateWorkLogForCheckOut(Long userId, Long matchingId,
      Double longitude, Double latitude);

  Page<WorkLog> getAllWorkLogsByManager(Long userId, Pageable pageable);
}
